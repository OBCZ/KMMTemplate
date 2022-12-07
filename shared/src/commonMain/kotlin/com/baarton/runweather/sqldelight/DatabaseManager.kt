package com.baarton.runweather.sqldelight

import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.model.weather.WeatherData

interface DatabaseManager {

    /**
     * Returns all database entries.
     *
     * @return A collection of [PersistedWeather] objects.
     */
    fun getAll(): List<PersistedWeather>

    /**
     * Inserts a database entry. This is done asynchronously and database is cleared every time before an insert.
     *
     * @param weatherData Entry to insert.
     */
    suspend fun insert(weatherData: WeatherData)

    /**
     * Clears the database. Asynchronous.
     */
    suspend fun nuke()

}