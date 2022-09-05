package com.baarton.runweather.models.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/*
 * All units are intended to be metric and the requests sent should be kept to reflect this:
 * > temperature [Kelvin]
 * > pressure [hPa]
 * > humidity [%]
 * > speed [m/s]
 * > deg [°]
 * > oneHour [mm]
 * > threeHour [mm]
 */
@Serializable
data class WeatherData(

    @SerialName("weather")
    val weatherList: List<Weather>,
    @SerialName("name")
    val locationName: String,
    @SerialName("main")
    val mainData: MainData,
    @SerialName("wind")
    val wind: Wind,
    @SerialName("rain")
    val rain: Rain? = null,
    @SerialName("sys")
    val sys: Sys
) {

    @Serializable
    data class MainData(

        /*
         * Units in [Kelvin].
         */
        @SerialName("temp")
        val temperature: String,

        /*
         * Units in [hPa].
         */
        @SerialName("pressure")
        val pressure: String,

        /*
         * Relative in [%].
         */
        @SerialName("humidity")
        val humidity: String
    )

    @Serializable
    data class Wind(

        /*
         * Units in [m/s].
         */
        @SerialName("speed")
        val speed: String,

        /*
         * Units in [(0 - 360)°].
         */
        @SerialName("deg")
        val deg: String
    )

    @Serializable
    data class Rain(

        /*
         * Units in [mm].
         */
        @SerialName("1h")
        val oneHour: String? = null,

        /*
         * Units in [mm].
         */
        @SerialName("3h")
        val threeHour: String? = null
    )

    @Serializable
    data class Sys(

        /*
         * Unix timestamp in <seconds> format.
         */
        @SerialName("sunrise")
        val sunrise: String,

        /*
         * Unix timestamp in <seconds> format.
         */
        @SerialName("sunset")
        val sunset: String
    )

}