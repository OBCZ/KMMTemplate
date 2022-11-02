package com.baarton.runweather.model.weather

import com.baarton.runweather.ktor.AngleSerializer
import com.baarton.runweather.ktor.HeightSerializer
import com.baarton.runweather.ktor.HumiditySerializer
import com.baarton.runweather.ktor.PressureSerializer
import com.baarton.runweather.ktor.SecondsInstantSerializer
import com.baarton.runweather.ktor.TemperatureSerializer
import com.baarton.runweather.ktor.VelocitySerializer
import com.baarton.runweather.model.Angle
import com.baarton.runweather.model.Height
import com.baarton.runweather.model.Height.Companion.mm
import com.baarton.runweather.model.Humidity
import com.baarton.runweather.model.Humidity.Companion.percent
import com.baarton.runweather.model.Pressure
import com.baarton.runweather.model.Pressure.Companion.hpa
import com.baarton.runweather.model.Temperature
import com.baarton.runweather.model.Temperature.Companion.kelvin
import com.baarton.runweather.model.Velocity
import kotlinx.datetime.Instant
import kotlinx.datetime.isDistantFuture
import kotlinx.datetime.isDistantPast
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
 * All units are intended to be metric when received and the requests sent should be kept as such to reflect this:
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
    val rain: Rain = Rain(),
    @SerialName("sys")
    val sys: Sys
) {

    fun isInvalid(): Boolean {
        return weatherList.isEmpty() || locationName.isBlank() || mainData.isInvalid() || wind.isInvalid() || rain.isInvalid() || sys.isInvalid()
    }

    @Serializable
    data class MainData(

        /*
         * Units in [Kelvin].
         */
        @SerialName("temp")
        @Serializable(with = TemperatureSerializer::class)
        val temperature: Temperature,

        /*
         * Units in [hPa].
         */
        @SerialName("pressure")
        @Serializable(with = PressureSerializer::class)
        val pressure: Pressure,

        /*
         * Relative in [%].
         */
        @SerialName("humidity")
        @Serializable(with = HumiditySerializer::class)
        val humidity: Humidity
    ) {
        fun isInvalid(): Boolean {
            return temperature.kelvin.value < 0 || pressure.hpa.value < 0 || humidity.percent.value < 0 || humidity.percent.value > 100
        }
    }

    @Serializable
    data class Wind(

        /*
         * Units in [m/s].
         */
        @SerialName("speed")
        @Serializable(with = VelocitySerializer::class)
        val velocity: Velocity,

        /*
         * Units in [(0 - 360)°].
         */
        @SerialName("deg")
        @Serializable(with = AngleSerializer::class)
        val angle: Angle
    ) {
        fun isInvalid(): Boolean {
            return velocity.value < 0 || angle.value < 0 || angle.value > 360
        }
    }

    @Serializable
    data class Rain(

        /*
         * Units in [mm].
         */
        @SerialName("1h")
        @Serializable(with = HeightSerializer::class)
        val oneHour: Height = 0.mm,

        /*
         * Units in [mm].
         */
        @SerialName("3h")
        @Serializable(with = HeightSerializer::class)
        val threeHour: Height = 0.mm

    ) {
        fun isInvalid(): Boolean {
            return oneHour.value < 0 || threeHour.value < 0
        }
    }

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