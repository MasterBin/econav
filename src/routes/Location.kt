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

/**
 * Created by n.samoylov on 18.11.2020
 */

fun Route.location(routeService: RouteService) {

    post<RouteRequest>("/route/") {
        call.respond(
            RouteResponse(routeService.getRoute(start = it.start, end = it.end))
        )
    }

}

@Serializable
data class RouteRequest(
    val start : LatLon,
    val end : LatLon
)

@Serializable
data class RouteResponse(
    val encodedRoute : String
)