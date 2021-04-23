package ru.nk.econav.routes

import io.ktor.application.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.nk.econav.model.LatLon
import ru.nk.econav.serivice.EcoPlacesService


fun Route.ecoPlaces(ecoPlacesService : EcoPlacesService) {

    get("/places") {
        val south  = call.parameters["south"]!!.toDouble()
        val north  = call.parameters["north"]!!.toDouble()
        val west  = call.parameters["west"]!!.toDouble()
        val east  = call.parameters["east"]!!.toDouble()

        call.respond(
            listOf<EcoPlaceJson>(EcoPlaceJson(LatLon(55.752121 , 37.617664)))//ecoPlacesService.getPlacesWithinBox(south, north, west, east)
        )
    }
}


@Serializable
data class EcoPlaceJson(
    @SerialName("location") val location : LatLon
)