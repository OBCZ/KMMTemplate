package com.baarton.runweather.ktor

import com.baarton.runweather.model.weather.WeatherData

interface WeatherDataApi {

    suspend fun getWeatherFromApi(): WeatherData
}