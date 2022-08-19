package com.baarton.runweather.models

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

//TODO cleanup

// @Entity(tableName = "current_weather")
@Serializable
data class WeatherData(

    /*
     * Database primary key. Assigned on INSERT. Does not come from the API.
     */
    // @PrimaryKey
    // val dbId: Int,

    /*
     * Record timestamp. Assigned before an API request. Does not come from the API.
     */
    // var timestamp: Long,

    // @ColumnInfo(name = "weatherList")
    @SerialName("weather")
    val weatherList: List<Weather>,

    // @ColumnInfo(name = "locationName")
    @SerialName("name")
    val locationName: String,

    // @Embedded
    @SerialName("main")
    val mainData: MainData,

    // @Embedded
    @SerialName("wind")
    val wind: Wind,

    // @Embedded
    @SerialName("rain")
    val rain: Rain? = null,

    // @Embedded
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