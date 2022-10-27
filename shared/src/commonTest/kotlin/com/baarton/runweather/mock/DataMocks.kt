package com.baarton.runweather.mock

import com.baarton.runweather.model.weather.Weather
import com.baarton.runweather.model.weather.WeatherData
import com.baarton.runweather.model.weather.WeatherId.*
import kotlinx.datetime.Instant

sealed class MockResponses {
    abstract val data: WeatherData
}

object CORRUPT : MockResponses() {
    override val data by lazy {
        WeatherData(
            listOf(Weather(UNKNOWN, "Clear", "clear sky", "01d")),
            "Brno_Corrupt",
            WeatherData.MainData("265.90", "", "45"),
            WeatherData.Wind("", "345"),
            null,
            WeatherData.Sys(Instant.fromEpochSeconds(1646803774), Instant.fromEpochSeconds(0))
        )
    }
}

object BRNO1 : MockResponses() {
    override val data by lazy {
        WeatherData(
            listOf(Weather(CLEAR_SKY, "Clear", "clear sky", "01d")),
            "Brno1",
            WeatherData.MainData("265.90", "1021", "45"),
            WeatherData.Wind("4.6", "345"),
            null,
            WeatherData.Sys(Instant.fromEpochSeconds(1646803774), Instant.fromEpochSeconds(1646844989))
        )
    }
}

object BRNO2 : MockResponses() {
    override val data by lazy {
        WeatherData(
            listOf(Weather(CLEAR_SKY, "Clear", "clear sky", "01d")),
            "Brno2",
            WeatherData.MainData("260.90", "1025", "55"),
            WeatherData.Wind("4.7", "355"),
            null,
            WeatherData.Sys(Instant.fromEpochSeconds(1646806774), Instant.fromEpochSeconds(1646842989))
        )
    }
}

object BRNO3 : MockResponses() {
    override val data by lazy {
        WeatherData(
            listOf(Weather(CLEAR_SKY, "Clear", "clear sky", "01d")),
            "Brno3",
            WeatherData.MainData("268.90", "1020", "35"),
            WeatherData.Wind("4.5", "305"),
            null,
            WeatherData.Sys(Instant.fromEpochSeconds(1646800774), Instant.fromEpochSeconds(1646849989))
        )
    }
}

object BRNO4 : MockResponses() {
    override val data by lazy {
        WeatherData(
            listOf(
                Weather(HEAVY_INTENSITY_RAIN, "Rain", "heavy rain", "05d"),
                Weather(LIGHT_RAIN, "Light Rain", "light rain", "08d")
            ),
            "Brno Rain",
            WeatherData.MainData("268.90", "1020", "35"),
            WeatherData.Wind("4.5", "305"),
            WeatherData.Rain("1", "3"),
            WeatherData.Sys(Instant.fromEpochSeconds(1646800774), Instant.fromEpochSeconds(1646849989))
        )
    }
}