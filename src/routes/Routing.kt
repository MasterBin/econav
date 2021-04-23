package ru.nk.econav.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import ru.nk.econav.model.LatLon
import ru.nk.econav.serivice.RouteService



fun Route.routingEngine(routeService: RouteService) {

    post<RouteRequest>("/route") {
        check(it.ecoParam in 0f..1f)

        call.respond(
            RouteResponse(routeService.getRoute(start = it.start, end = it.end, ecoParam = it.ecoParam))
        )
    }
}

@Serializable
data class RouteRequest(
    val start : LatLon,
    val end : LatLon,
    val ecoParam : Float = 1f
)


@Serializable
data class RouteResponse(
    val encodedRoute : String
)