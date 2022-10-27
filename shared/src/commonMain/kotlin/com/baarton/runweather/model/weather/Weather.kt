package com.baarton.runweather.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Weather(
    @SerialName("id")
    @Serializable(with = WeatherIdSerializer::class)
    val weatherId: WeatherId,
    @SerialName("main")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val iconId: String
)