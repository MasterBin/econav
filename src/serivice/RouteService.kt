package ru.nk.econav.serivice

import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.Geometry
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.sql.transactions.transaction
import ru.nk.econav.model.LatLon
import ru.nk.econav.util.execAndMap
import java.sql.Connection

class RouteService(
    private val geoContext: GeoApiContext
) {

    suspend fun getRoute(start: LatLon, end: LatLon): String {
        return transaction {
            """
                with astar as (select * from pgr_astar(
                    'select gid as id, source, target, cost, x1, y1, x2, y2 from ways',
                    (select source
                                    from ways
                                    order by the_geom <-> st_setsrid(st_makepoint(${start.lon}, ${start.lat}), 4326)
                                    limit 1),
                    (select source
                                    from ways
                                    order by the_geom <-> st_setsrid(st_makepoint(${end.lon}, ${end.lat}), 4326)
                                    limit 1),
                               directed := true
                    ))
                select st_asencodedpolyline(st_linemerge(st_collect(w.the_geom))) as res from astar as a, ways as w
                where a.edge = w.gid, 
            """
                .trimIndent().execAndMap { rs ->
                    rs.getString("res")
                }[0]
        }
    }


    @Serializable
    data class Geo(
        val type : String,
        val coordinates : List<List<Double>>
    )
}