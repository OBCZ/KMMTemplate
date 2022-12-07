package com.baarton.runweather.repo

import com.baarton.runweather.ktor.ImageUrlBuilder
import com.baarton.runweather.model.weather.CurrentWeather
import com.baarton.runweather.sensor.location.Location


interface WeatherRepository {

    companion object {
        internal const val DB_TIMESTAMP_KEY = "DbTimestampKey"

        fun getImageUrl(imageId: String): String {
            return ImageUrlBuilder.buildUrl(imageId)
        }
    }

    /**
     * Fetches a [CurrentWeather] object regardless on the source of the data (API, DB). Nullable. If null, there has been an error somewhere down the way or the DB is empty.
     *
     * @param location [Location] object representing the current (or cached) location of the device to request the weather for. If null, device location is not known (yet).
     * @return [CurrentWeather] object representing current weather at a given [location].
     */
    suspend fun refreshWeather(location: Location?): CurrentWeather?

    class WeatherAPIException(message: String, cause: Throwable?) : Exception(message, cause)
    class WeatherDataConsistencyException(message: String) : Exception(message)
    class LocationConsistencyException(message: String) : Exception(message)

}