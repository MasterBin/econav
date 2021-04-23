package ru.nk.econav.util


fun ClosedFloatingPointRange<Double>.convert(number: Double, target: ClosedFloatingPointRange<Double>): Double {
    val ratio = number / (endInclusive - start)
    return (ratio * (target.endInclusive - target.start))
}