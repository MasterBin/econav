package ru.nk.econav.serivice

import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.LatLng
import com.graphhopper.util.Instruction
import ru.nk.econav.model.LatLon
import ru.nk.econav.routes.RouteResponse
import ru.nk.econav.util.UnitsUtils
import util.Upshot
import util.asError
import util.asOk
import util.fold
import java.util.*

class RouteService(
    private val routingManager: RoutingManager
) {

    suspend fun getRoute(start: LatLon, end: LatLon, ecoParam: Float = 1f): RouteResponse {

        return routingManager.routeByPoints(start, end, ecoParam.toDouble())
            .fold(
                { path ->
                    val encoded = PolylineEncoding.encode(path.points.map { LatLng(it.lat, it.lon) })
                    RouteResponse(
                        encodedRoute = encoded,
                        distance = UnitsUtils.convertMetersToHuman(path.distance),
                        time = UnitsUtils.convertMsToHuman(path.time),
                        instructions = path.instructions.map { it.toJsonInstruction() }
                    )
                },
                { lst ->
                    lst.firstOrNull()?.let { throw it } ?: error("Internal Server Error")
                }
            )
    }

    private val tr = routingManager.graphHopper.translationMap.get("ru")

    private fun Instruction.toJsonInstruction(): ru.nk.econav.routes.Instruction = ru.nk.econav.routes.Instruction(
        turnDescription = getTurnDescription(tr),
        distance = UnitsUtils.convertMetersToHuman(distance),
        time = UnitsUtils.convertMsToHuman(time),
        points = points.map { LatLon(it.lat, it.lon) }
    )


}