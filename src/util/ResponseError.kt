package ru.nk.econav.util

import kotlinx.serialization.Serializable


@Serializable
data class ResponseError(
    val text : String,
)