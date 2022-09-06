package com.baarton.runweather.repo

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.baarton.runweather.Config
import com.baarton.runweather.ktor.WeatherApi
import com.baarton.runweather.models.SettingsViewModel.Companion.WEATHER_DATA_THRESHOLD_TAG
import com.baarton.runweather.models.weather.CurrentWeather
import com.baarton.runweather.sqldelight.DatabaseHelper
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class WeatherRepository(
    private val dbHelper: DatabaseHelper,
    private val settings: Settings,
    private val config: Config,
    private val weatherApi: WeatherApi,
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

    fun getWeather(): Flow<CurrentWeather?> {
        log.d("Get WeatherData from DB.")
        return dbHelper.getAll().map {
            it?.let { CurrentWeather(it, getLastDownloadTime()) }
        }
    }

    suspend fun refreshWeather() {
        if (isWeatherListStale()) {
            doRefreshWeather()
        }
    }

    private suspend fun doRefreshWeather() {
        val weatherResult = weatherApi.getJsonFromApi()
        log.d { "Weather network result: $weatherResult" }
        settings.putLong(DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())

        if (weatherResult.locationName.isNotBlank()) {
            dbHelper.insert(weatherResult)
            log.d { "WeatherData result put to DB." }
        }
    }

    private fun isWeatherListStale(): Boolean {
        val lastDownload = getLastDownloadTime()
        val threshold = Duration.parseIsoString(settings.getString(WEATHER_DATA_THRESHOLD_TAG, config.weatherDataMinimumThreshold.toIsoString()))
        val now = clock.now().toEpochMilliseconds().milliseconds
        log.d { "Resolving staleness of data.\n" +
            "Saved data timestamp: $lastDownload\n" +
            "-------Timestamp now: $now\n" +
            "-----------Threshold: $threshold" }
        return (lastDownload + threshold < now).also {
            if (!it) {
                log.i { "Weather not fetched from network. Recently updated" }
            } else {
                log.i { "Weather data is stale. Will fetch from network." }
            }
        }
    }

    private fun getLastDownloadTime(): Duration {
        return settings.getLong(DB_TIMESTAMP_KEY, 0).milliseconds
    }

}