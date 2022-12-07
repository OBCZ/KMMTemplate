package com.baarton.runweather.ktor

import com.baarton.runweather.model.weather.WeatherData
import com.baarton.runweather.sensor.location.Location

interface WeatherDataApi {

    /**
     * Builds an OpenWeatherAPI compatible URL for a data request and parses the returned JSON data into an [WeatherData] object.
     *
     * @param location [Location] object representing the current (or cached) location of the device to request the weather for.
     * @return [WeatherData] object representing current weather at given [location].
     *
     * Example URLs:
     * https://api.openweathermap.org/data/2.5/weather?appid=xyz&lang=cz&units=standard&lat=50.0&lon=15.0
     * https://api.openweathermap.org/data/2.5/onecall?appid=xyz&lat=50&lon=15&exclude=minutely,hourly,daily,alerts
     * https://api.openweathermap.org/data/2.5/weather?appid=xyz&lat=50&lon=15
     */
    suspend fun getWeatherFromApi(location: Location): WeatherData
}