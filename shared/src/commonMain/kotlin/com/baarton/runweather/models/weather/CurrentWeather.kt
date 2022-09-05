package com.baarton.runweather.models.weather

import com.baarton.runweather.db.PersistedWeather
import kotlin.time.Duration


data class CurrentWeather(
    val persistedWeather: PersistedWeather,
    val timestamp: Duration,
)