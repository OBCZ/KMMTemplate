package com.baarton.runweather.models.weather

import com.baarton.runweather.db.PersistedWeather
import kotlin.time.Duration


data class CurrentWeather(
    val persistedWeather: PersistedWeather,
    val timestamp: Duration,
) {

    fun isEmptyOrIncomplete(): Boolean {
        return with(persistedWeather) {
            weatherList.isEmpty() || locationName.isBlank() || mainData.isBlank() || wind.isBlank() || sys.isBlank()
        }
    }

}