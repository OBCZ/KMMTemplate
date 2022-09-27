package com.baarton.runweather.models.weather

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

    fun isEmptyOrIncomplete(): Boolean {
        return with(persistedWeather) {
            weatherList.isEmpty() || locationName.isBlank() || mainData.isBlank() || wind.isBlank() || sys.isBlank()
        }
    }

}