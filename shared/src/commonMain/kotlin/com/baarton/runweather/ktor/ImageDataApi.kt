package com.baarton.runweather.ktor

interface ImageDataApi {

    /**
     * Builds an OpenWeatherAPI compatible URL for an image request.
     *
     * @param imageId OpenWeatherAPI compatible image ID for requesting. See https://openweathermap.org/weather-conditions.
     * @return Built URL.
     *
     * Example URL:
     * "https://openweathermap.org/img/wn/04d@2x.png"
     */
    fun buildUrl(imageId: String): String
}