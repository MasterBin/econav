package ru.nk.econav.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import ru.nk.econav.model.LatLon
import ru.nk.econav.serivice.RouteService
import util.getOrNull


fun Route.routingEngine(routeService: RouteService) {

    post<RouteRequest>("/route") {
        require(it.ecoParam in 0f..1f)

        call.respond(
            routeService.getRoute(
                start = it.start,
                end = it.end,
                ecoParam = it.ecoParam
            )
        )
    }
}


@Serializable
data class RouteRequest(
    val start: LatLon,
    val end: LatLon,
    val ecoParam: Float = 1f
)

@Serializable
data class RouteResponse(
    val encodedRoute: String,
    val distance: String,
    val time: String,
    val instructions : List<Instruction>
)

@Serializable
data class Instruction(
    val turnDescription : String,
    val distance : String,
    val time : String,
    val points : List<LatLon>
)