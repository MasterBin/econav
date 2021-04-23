package database.tables

import database.geotypes.geometry
import database.geotypes.hstore
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object EcoPlaces : IntIdTable("eco_places") {

//    val rating = double("rating")

    val name = text("name").nullable()
    val description = text("description").nullable()
    val geometry = geometry("geom")

    val type = text("type")
    val processed = bool("processed").default(false)
//    val tags = hstore("tags")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class EcoPlace(id : EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EcoPlace>(EcoPlaces)

    var name by EcoPlaces.name
    var description by EcoPlaces.description
    var geometry by EcoPlaces.geometry
//    var rating by EcoPlaces.rating
    var processed by EcoPlaces.processed

    var type by EcoPlaces.type
}





