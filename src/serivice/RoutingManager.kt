package ru.nk.econav.serivice

import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.LatLng
import com.graphhopper.GHRequest
import com.graphhopper.GraphHopper
import com.graphhopper.util.Parameters
import graphhopper.GraphHopperFactory
import graphhopper.MyGraphHopper
import ru.nk.econav.model.LatLon
import util.Upshot
import util.asError
import util.asOk


class RoutingManager {

    private var graphHopper : GraphHopper = GraphHopperFactory.create()

    suspend fun routeByPoints(from : LatLon, to : LatLon, ecoParam : Double) : Upshot<String, Unit> {
        require(ecoParam in 0.0..1.0)

        val req = GHRequest(
            from.lat,
            from.lon,
            to.lat,
            to.lon
        )
            .putHint(Parameters.CH.DISABLE, true)
            .putHint(MyGraphHopper.ecoParam, ecoParam)
            .setProfile("foot")

        val rsp = graphHopper.route(req)
        return if (!rsp.hasErrors()) {
            val path = rsp.best
            println("points : ${path.points}")
            println("distance : ${path.distance}")
            println("distance : ${path.time}")

            PolylineEncoding.encode(path.points.map { LatLng(it.lat, it.lon) }).also { println(it) }.asOk()
        } else {
            println(rsp.errors)
            Unit.asError()
        }
    }

}