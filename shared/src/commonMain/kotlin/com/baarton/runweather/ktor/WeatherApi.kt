package com.baarton.runweather.ktor

import com.baarton.runweather.model.weather.WeatherData

interface WeatherApi {
    suspend fun getJsonFromApi(): WeatherData
}