package com.baarton.runweather.models.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    @SerialName("id")
    val weatherId: String,
    @SerialName("main")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val iconId: String
)