package com.baarton.runweather.ktor

import com.baarton.runweather.models.WeatherData
import com.baarton.runweather.response.BreedResult

interface WeatherApi {
    suspend fun getJsonFromApi(): WeatherData
}
