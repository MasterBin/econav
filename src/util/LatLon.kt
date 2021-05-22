package ru.nk.econav.util

import kotlinx.serialization.Serializable

/**
 * Created by n.samoylov on 25.01.2021
 */
@Serializable
data class LatLon(
    val lat : Double,
    val lon : Double
)
