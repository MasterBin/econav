package ru.nk.econav.repository

import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import ru.nk.econav.model.LatLon
import util.Upshot

interface GeocodingRepository {
    fun search(query : String, autocomplete : Boolean,userPoint : LatLon?) : Upshot<List<CarmenFeature>, Unit>
}
