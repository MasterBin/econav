package ru.nk.econav.serivice

import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import ru.nk.econav.model.LatLon

class RouteService(
    private val geoContext: GeoApiContext
) {

    suspend fun getRoute(start: LatLon, end: LatLon): List<String>? {

        val res = try {
            DirectionsApi.newRequest(geoContext)
                .mode(TravelMode.WALKING)
                .origin(LatLng(start.lat, start.lon))
                .destination(LatLng(end.lat, end.lon))
                .alternatives(true)
                .await()
        } catch (ex: Exception) {
            println(ex.message)
            null
        }

        return res?.routes?.map { it.toString() }
    }


}