package ru.nk.econav.repository.impl

import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import ru.nk.econav.util.LatLon
import ru.nk.econav.repository.GeocodingRepository
import util.Upshot
import util.asError
import util.asOk

class GeocodingRepositoryImpl(
    private val accessToken : String
) : GeocodingRepository {

    private fun query(): MapboxGeocoding.Builder =
        MapboxGeocoding.builder()
            .accessToken(accessToken)

    override fun search(
        query: String,
        autocomplete: Boolean,
        userPoint: LatLon?
    ): Upshot<List<CarmenFeature>, Unit> =
        query().query(query)
            .limit(10)
            .autocomplete(autocomplete)
            .apply {
                userPoint?.let {
                    proximity(Point.fromLngLat(it.lon, it.lat))
                }
            }
            .build().executeCall().let {
                if (it.isSuccessful && it.body()?.features()?.size != 0) {
                    it.body()?.features()?.asOk() ?: Unit.asError()
                } else {
                    Unit.asError()
                }
            }

}