package com.baarton.runweather.ktor

import com.baarton.runweather.models.WeatherData

interface WeatherApi {
    suspend fun getJsonFromApi(): WeatherData
}