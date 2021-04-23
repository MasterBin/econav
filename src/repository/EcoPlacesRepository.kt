package ru.nk.econav.repository

import database.tables.EcoPlace
import org.opengis.geometry.BoundingBox
import org.postgis.PGbox2d

interface EcoPlacesRepository {
    fun processPlaces()
    fun getPlacesWithinBox(box : PGbox2d) : List<EcoPlace>
}