package ru.nk.econav.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import ru.nk.econav.util.LatLon
import ru.nk.econav.service.GeocodingService


fun Route.geocoding(service: GeocodingService) {

    this.get(path = "/geocoding/search") {
        require(call.parameters.contains("autocomplete"))
        require(call.parameters.contains("query"))

        val lat = call.parameters["userLatitude"]?.toDouble()
        val lon = call.parameters["userLongitude"]?.toDouble()
        val autoComplete = call.parameters["autocomplete"]!!.toBoolean()
        val query = call.parameters["query"]!!

        val userLocation = if (lat != null && lon != null) {
            LatLon(lat, lon)
        } else {
            null
        }

        call.respond(service.search(query, autoComplete, userLocation))
    }

}