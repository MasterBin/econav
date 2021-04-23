package ru.nk.econav.repository.impl

import database.geooperations.within
import database.geooperations.withinDistanceFrom
import database.tables.EcoPlace
import database.tables.EcoPlaces
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgis.Geometry
import org.postgis.PGbox2d
import ru.nk.econav.database.geooperations.intersects
import ru.nk.econav.database.tables.Road
import ru.nk.econav.database.tables.Roads
import ru.nk.econav.repository.EcoPlacesRepository
import java.math.RoundingMode

class EcoPlacesRepositoryImpl : EcoPlacesRepository {

    var i = 0

    override fun processPlaces() {
        transaction {
            SchemaUtils.create(AffectedRoads)
        }

        transaction {
            EcoPlace.find { EcoPlaces.processed eq false }.toList()
        }.forEach { place ->

            transaction inner@{

                if (place.type == "park" || place.type == "nature_reserve") {
                    processIntersects(place)
                    place.processed = true
                    return@inner
                }

                processIntersects(place)
                processWithDistance(place)

                place.processed = true
            }
        }

        transaction {
            processRoads()
        }
    }

    private fun Transaction.processRoads() {
        AffectedRoads
            .slice(AffectedRoads.roadId, AffectedRoads.rating, AffectedRoads.ratedTimes)
            .select { AffectedRoads.processed eq false }
            .forEach {
                val id = it[AffectedRoads.roadId]
                val ratingSum = it[AffectedRoads.rating]
                val times = it[AffectedRoads.ratedTimes]

                Road.findById(id)?.let { f->
                    f.priority =
                        ratingSum.toBigDecimal().divide(times.toBigDecimal(), 2, RoundingMode.HALF_UP).toFloat()
                }
            }
    }

    private fun Transaction.processWithDistance(place: EcoPlace) {
        val inf = getInfoByType(place.type)
        val distance = inf.affectMeters.toDouble()
        val rate = inf.ratingAround

        if (distance == 0.0) {
            return
        }

        Road.find {
            Roads.geometry.withinDistanceFrom(
                place.geometry,
                distance
            )
        }.forEach {
            updateAffectedRoads(it.id.value, rate)
        }
    }

    private fun Transaction.processIntersects(place: EcoPlace) {
        val rate = getInfoByType(place.type).ratingIn ?: 0

        if (place.geometry.getType() == Geometry.POLYGON ||
            place.geometry.getType() == Geometry.MULTIPOLYGON
        ) {
            Road.find {
                Roads.geometry.intersects(place.geometry)
            }.forEach {
                updateAffectedRoads(it.id.value, rate)
            }
        }
    }

    private fun getInfoByType(type: String): EcoPlaceProcessingConfig.Config {
        return EcoPlaceProcessingConfig.affectedInMeters[type] ?: error("")
    }

    override fun getPlacesWithinBox(box: PGbox2d): List<EcoPlace> {
        return transaction {
            EcoPlace.find { EcoPlaces.geometry within box }.toList()
        }
    }

    private fun updateAffectedRoads(roadIdd: Int, ratingg: Int) {
        transaction {
            val found = AffectedRoads.select { AffectedRoads.roadId eq roadIdd }
            if (found.count() == 0L) {
                AffectedRoads.insert {
                    it[roadId] = roadIdd
                    it[rating] = ratingg.toLong()
                    it[ratedTimes] = 1
                    it[processed] = false
                }
            } else {
                AffectedRoads.update({ AffectedRoads.roadId eq roadIdd }) {
                    with(SqlExpressionBuilder) {
                        it.update(rating, rating + ratingg.toLong())
                        it.update(ratedTimes, ratedTimes + 1)
                        it[processed] = false
                    }
                }
            }
        }
    }

    object AffectedRoads : Table("affected_roads") {
        val roadId = integer("road_id").uniqueIndex()
        val rating = long("rating")
        val ratedTimes = integer("rated_times")
        val processed = bool("processed")

        override val primaryKey: PrimaryKey = PrimaryKey(roadId)
    }

}


object EcoPlaceProcessingConfig {

    val affectedInMeters = mapOf(
        "park" to Config(0, 60, 100),

        "nature_reserve" to Config(10, 70, 100),

        "fuel" to Config(60, 30),

        "industrial" to Config(30, 20, 5),

        "motorway" to Config(150, 10),

        "trunk" to Config(100, 20),

        "primary" to Config(100, 20),

        "secondary" to Config(35, 20),

        "tertiary" to Config(18, 20),

        "motorway_link" to Config(150, 20),

        "trunk_link" to Config(100, 20),

        "primary_link" to Config(100, 20),

        "secondary_link" to Config(35, 20),

        "tertiary_link" to Config(18, 20),
    )

    data class Config(
        val affectMeters: Int,
        val ratingAround: Int,
        val ratingIn: Int? = null
    )
}