package com.baarton.runweather.model.weather

import com.baarton.runweather.ktor.SecondsInstantSerializer
import kotlinx.datetime.Instant
import kotlinx.datetime.isDistantFuture
import kotlinx.datetime.isDistantPast
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

    fun isEmptyOrIncomplete(): Boolean {
        return weatherList.isEmpty() || locationName.isBlank() || mainData.isBlank() || wind.isBlank() || sys.isInvalid()
    }

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
    ) {
        fun isBlank(): Boolean {
            return temperature.isBlank() || pressure.isBlank() || humidity.isBlank()
        }
    }

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
    ) {
        fun isBlank(): Boolean {
            return speed.isBlank() || deg.isBlank()
        }
    }

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
         * Instant serialized from Unix timestamp in <seconds> format.
         */
        @SerialName("sunrise")
        @Serializable(with = SecondsInstantSerializer::class)
        val sunrise: Instant,

        /*
         * Instant serialized from Unix timestamp in <seconds> format.
         */
        @SerialName("sunset")
        @Serializable(with = SecondsInstantSerializer::class)
        val sunset: Instant
    ) {
        fun isInvalid(): Boolean {
            return sunrise.isDistantPast || sunset.isDistantPast || sunrise.isDistantFuture || sunset.isDistantFuture
        }
    }
}