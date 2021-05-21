package ru.nk.econav.util

object UnitsUtils {

    fun convertMetersToHuman(distanceInMeters: Double): String {
        return when {
            distanceInMeters >= 1100 -> {
                "%.2f Км".format((distanceInMeters / 1000))
            }
            else -> {
                "%.0f м".format(distanceInMeters)
            }
        }
    }

    fun convertMsToHuman(timeInMs : Long) : String {
        return when (timeInMs) {
            in 1000..59999 -> {
                "%.0f сек".format(timeInMs / 1000.0)
            }
            in 60000..3599999 -> {
                "%.0f мин".format(timeInMs / 60_000.0)
            }
            else -> {
                "%.2f ч".format(timeInMs / 3_600_000.0)
            }
        }
    }
}