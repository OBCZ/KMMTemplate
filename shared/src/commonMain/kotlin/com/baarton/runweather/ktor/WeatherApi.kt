package com.baarton.runweather.ktor

import com.baarton.runweather.models.weather.WeatherData

interface WeatherApi {
    suspend fun getJsonFromApi(): WeatherData
}