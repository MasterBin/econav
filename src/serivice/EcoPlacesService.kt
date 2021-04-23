package ru.nk.econav.serivice

import org.postgis.PGbox2d
import org.postgis.Point
import ru.nk.econav.model.LatLon
import ru.nk.econav.repository.EcoPlacesRepository
import ru.nk.econav.routes.EcoPlaceJson

class EcoPlacesService(
    private val repository: EcoPlacesRepository
) {

    fun getPlacesWithinBox(
        south: Double,
        north: Double,
        west: Double,
        east: Double
    ) : List<EcoPlaceJson> {

        return repository.getPlacesWithinBox(
            PGbox2d(Point(east,south), Point(west,north))
        ).map {
            EcoPlaceJson(
                LatLon(
                    it.geometry.firstPoint.getY(),
                    it.geometry.firstPoint.getX()
                )
            )
        }

    }


}