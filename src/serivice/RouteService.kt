package ru.nk.econav.serivice

import ru.nk.econav.model.LatLon
import util.fold

class RouteService(
    private val routingManager : RoutingManager
) {

    suspend fun getRoute(start: LatLon, end: LatLon): String {
        return routingManager.routeByPoints(start, end, 1.0).fold(
            { it },
            { "" }
        )
    }
}