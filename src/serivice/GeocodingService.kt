package ru.nk.econav.serivice

import com.vividsolutions.jts.geom.Coordinate
import kotlinx.serialization.Serializable
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.CRS
import ru.nk.econav.model.LatLon
import ru.nk.econav.repository.GeocodingRepository
import ru.nk.econav.util.UnitsUtils
import util.getOr
import util.map

class GeocodingService(
    private val repository : GeocodingRepository
) {

    private val EPSG4326 = "GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]"

    private val crs = CRS.parseWKT(EPSG4326)

    fun search(query : String, autocomplete : Boolean, userLocation : LatLon?) : GeoResponse{

        val res = repository.search(query, autocomplete, userLocation)

        return res.map { lst ->
            GeoResponse(
                features = lst.map { f ->
                    GeoFeature(
                        name = f.text()!!,
                        address = f.placeName()!!,
                        center = f.center()!!.let { LatLon(it.latitude(), it.longitude()) },
                        bbox = f.bbox()?.let { BoundingBox(it.east(), it.north(), it.west(), it.south()) },
                        matchingText = f.matchingText(),
                        distanceTo = userLocation?.let { UnitsUtils.convertMetersToHuman(JTS.orthodromicDistance(Coordinate(it.lon, it.lat), Coordinate(f.center()!!.longitude(), f.center()!!.latitude()), crs)) }
                    )
                }.toList()
            )
        }.getOr {
            GeoResponse(emptyList())
        }
    }

}

@Serializable
data class GeoResponse(
    val features : List<GeoFeature>
)

@Serializable
data class GeoFeature(
    val name : String,
    val address : String,
    val center : LatLon,
    val bbox : BoundingBox?,
    val matchingText : String?,
    val distanceTo : String?
)

@Serializable
data class BoundingBox(
    val east : Double,
    val north : Double,
    val west : Double,
    val south : Double
)