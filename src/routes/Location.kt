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

    get("/") {
        call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
    }

    post<RouteRequest>("/route/") {
//        withContext(Dispatchers.IO) {
        call.respond(
            routeService.getRoute(it.start, it.end) ?: emptyList<String>()
        )
//        }
    }
}

@Serializable
data class RouteRequest(
    val start : LatLon,
    val end : LatLon
)