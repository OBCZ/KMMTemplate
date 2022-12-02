package com.baarton.runweather.ktor

import com.baarton.runweather.sensor.location.Location
import com.baarton.runweather.model.weather.WeatherData

interface WeatherDataApi {

    suspend fun getWeatherFromApi(location: Location): WeatherData
}