package ru.nk.econav.serivice

import com.graphhopper.GHRequest
import com.graphhopper.GraphHopper
import com.graphhopper.ResponsePath
import com.graphhopper.util.Parameters
import graphhopper.GraphHopperFactory
import graphhopper.MyGraphHopper
import ru.nk.econav.model.LatLon
import ru.nk.econav.repository.EcoPlacesRepository
import util.Upshot
import util.asError
import util.asOk


class RoutingManager(
    private val ecoPlacesRepository: EcoPlacesRepository
) {

    var graphHopper : GraphHopper

    init {
        processEcoPlaces()
        graphHopper = GraphHopperFactory.create()
    }

    suspend fun routeByPoints(from : LatLon, to : LatLon, ecoParam : Double) : Upshot<ResponsePath, List<Throwable>> {
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
            .setAlgorithm(Parameters.Algorithms.ASTAR) //TODO: check other algorithm

        val rsp = graphHopper.route(req)

        return if (!rsp.hasErrors()) {
            rsp.best.instructions.first()?.extraInfoJSON?.keys?.also { println(it) }
            (rsp.all.minByOrNull { it.time; } ?: error("")).asOk()
        } else {
            rsp.errors.asError()
        }
    }

    private fun processEcoPlaces() {
        ecoPlacesRepository.processPlaces()
    }

}