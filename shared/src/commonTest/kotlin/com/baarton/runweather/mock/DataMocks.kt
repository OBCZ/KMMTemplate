package com.baarton.runweather.mock

import com.baarton.runweather.model.Angle.Companion.deg
import com.baarton.runweather.model.Height.Companion.mm
import com.baarton.runweather.model.Humidity.Companion.percent
import com.baarton.runweather.model.Pressure.Companion.hpa
import com.baarton.runweather.model.Temperature.Companion.kelvin
import com.baarton.runweather.model.Velocity.Companion.mps
import com.baarton.runweather.model.weather.Weather
import com.baarton.runweather.model.weather.WeatherData
import com.baarton.runweather.model.weather.WeatherId.*
import kotlinx.datetime.Instant

sealed class MockResponse {
    abstract val data: WeatherData
}

object CORRUPT : MockResponse() {
    override val data by lazy {
        WeatherData(
            listOf(Weather(UNKNOWN, "Clear", "clear sky", "01d")),
            "Brno_Corrupt",
            WeatherData.MainData(265.90.kelvin, (-200).hpa, 45.percent),
            WeatherData.Wind((-5).mps, 345.deg),
            WeatherData.Rain(),
            WeatherData.Sys(Instant.fromEpochSeconds(1646803774), Instant.fromEpochSeconds(0))
        )
    }
}

object BRNO1 : MockResponse() {
    override val data by lazy {
        WeatherData(
            listOf(Weather(CLEAR_SKY, "Clear", "clear sky", "01d")),
            "Brno1",
            WeatherData.MainData(265.90.kelvin, 1021.hpa, 45.percent),
            WeatherData.Wind(4.6.mps, 345.deg),
            WeatherData.Rain(),
            WeatherData.Sys(Instant.fromEpochSeconds(1646803774), Instant.fromEpochSeconds(1646844989))
        )
    }
}

object BRNO2 : MockResponse() {
    override val data by lazy {
        WeatherData(
            listOf(Weather(CLEAR_SKY, "Clear", "clear sky", "01d")),
            "Brno2",
            WeatherData.MainData(260.90.kelvin, 1025.hpa, 55.percent),
            WeatherData.Wind(4.7.mps, 355.deg),
            WeatherData.Rain(),
            WeatherData.Sys(Instant.fromEpochSeconds(1646806774), Instant.fromEpochSeconds(1646842989))
        )
    }
}

object BRNO3 : MockResponse() {
    override val data by lazy {
        WeatherData(
            listOf(Weather(CLEAR_SKY, "Clear", "clear sky", "01d")),
            "Brno3",
            WeatherData.MainData(268.90.kelvin, 1020.hpa, 35.percent),
            WeatherData.Wind(4.5.mps, 305.deg),
            WeatherData.Rain(),
            WeatherData.Sys(Instant.fromEpochSeconds(1646800774), Instant.fromEpochSeconds(1646849989))
        )
    }
}

object BRNO4 : MockResponse() {
    override val data by lazy {
        WeatherData(
            listOf(
                Weather(HEAVY_INTENSITY_RAIN, "Rain", "heavy rain", "05d"),
                Weather(LIGHT_RAIN, "Light Rain", "light rain", "08d")
            ),
            "Brno Rain",
            WeatherData.MainData(268.90.kelvin, 1020.hpa, 35.percent),
            WeatherData.Wind(4.5.mps, 305.deg),
            WeatherData.Rain(1.mm, 3.mm),
            WeatherData.Sys(Instant.fromEpochSeconds(1646800774), Instant.fromEpochSeconds(1646849989))
        )
    }
}