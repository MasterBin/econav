package ru.nk.econav.repository

import org.opengis.geometry.BoundingBox


interface RoadsRepository {

    fun getRoadsWithinBox(box : BoundingBox)
}