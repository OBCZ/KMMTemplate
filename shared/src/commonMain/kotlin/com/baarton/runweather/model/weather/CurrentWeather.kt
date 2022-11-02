package com.baarton.runweather.model.weather

import com.baarton.runweather.db.PersistedWeather
import kotlin.time.Duration


data class CurrentWeather(
    val persistedWeather: PersistedWeather,
    val timestamp: Duration,
) {

    constructor(weatherData: WeatherData, timestamp: Duration) : this(
        PersistedWeather(
            weatherData.weatherList,
            weatherData.locationName,
            weatherData.mainData,
            weatherData.wind,
            weatherData.rain,
            weatherData.sys
        ),
        timestamp
    )

    fun isInvalid(): Boolean {
        return with(persistedWeather) {
            weatherList.isEmpty() || locationName.isBlank() || mainData.isInvalid() || wind.isInvalid() || rain.isInvalid() || sys.isInvalid()
        }
    }

}