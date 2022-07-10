package com.baarton.runweather.ktor

import com.baarton.runweather.models.WeatherData
import com.baarton.runweather.response.BreedResult

interface DogApi {
    suspend fun getJsonFromApi(): WeatherData
}
