package com.baarton.runweather.models

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.baarton.runweather.DatabaseHelper
import com.baarton.runweather.db.CurrentWeather
import com.baarton.runweather.ktor.DogApi
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class WeatherRepository(
    private val dbHelper: DatabaseHelper,
    private val settings: Settings,
    private val dogApi: DogApi,
    log: Logger,
    private val clock: Clock
) {

    private val log = log.withTag("WeatherModel")

    companion object {
        internal const val DB_TIMESTAMP_KEY = "DbTimestampKey"
    }

    init {
        ensureNeverFrozen()
    }

    fun getWeather(): Flow<List<CurrentWeather>> = dbHelper.getAll()

    suspend fun refreshWeatherIfStale() {
        if (isWeatherListStale()) {
            refreshWeather()
        }
    }

    suspend fun refreshWeather() {
        val weatherResult = dogApi.getJsonFromApi()
        log.v { "Weather network result: ${weatherResult.toString()}" }
        // val breedList = breedResult.message.keys.sorted().toList()
        // log.v { "Fetched ${breedList.size} breeds from network" }
        settings.putLong(DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())

        if (weatherResult.locationName.isNotBlank()) {
            dbHelper.insert(weatherResult)
        }
    }

    // suspend fun updateBreedFavorite(breed: Breed) {
    //     dbHelper.updateFavorite(breed.id, !breed.favorite)
    // }

    private fun isWeatherListStale(): Boolean {
        val lastDownloadTimeMS = settings.getLong(DB_TIMESTAMP_KEY, 0)
        val oneHourMS = 60 * 60 * 1000
        val stale = lastDownloadTimeMS + oneHourMS < clock.now().toEpochMilliseconds()
        if (!stale) {
            log.i { "Weather not fetched from network. Recently updated" }
        }
        return stale
    }
}
