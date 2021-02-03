package ru.nk.econav.serivice

import com.google.maps.GeoApiContext
import org.jetbrains.exposed.sql.transactions.transaction
import ru.nk.econav.model.LatLon
import ru.nk.econav.util.execAndMap

class RouteService(
    private val geoContext: GeoApiContext
) {

    suspend fun getRoute(start: LatLon, end: LatLon): List<String> {
        return transaction {
            """select st_asencodedpolyline(res.the_geom) as res
from (select osm.the_geom
      from pgr_astar(
                   'select osm_id as id, source, target, cost, x1, y1, x2, y2 from ways',
                   (select source
                    from ways
                    order by the_geom  <-> st_setsrid(st_makepoint(${start.lon}, ${start.lat}), 4326)
                    limit 1),
                   (select source
                    from ways
                    order by the_geom <-> st_setsrid(st_makepoint(${end.lon}, ${end.lat}), 4326)
                    limit 1),
                   directed := false
               ) ast
               inner join osm_ways osm on ast.edge = osm.osm_id

      order by path_seq
     ) as res
            """
                .trimIndent().execAndMap { rs ->
                    rs.getString("res")
                }
        }
    }

}