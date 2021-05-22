package ru.nk.econav.repository

import com.mapbox.api.geocoding.v5.models.CarmenFeature
import ru.nk.econav.util.LatLon
import util.Upshot

interface GeocodingRepository {
    fun search(query : String, autocomplete : Boolean,userPoint : LatLon?) : Upshot<List<CarmenFeature>, Unit>
}
