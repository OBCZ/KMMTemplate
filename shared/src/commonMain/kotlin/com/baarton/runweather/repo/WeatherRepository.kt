package com.baarton.runweather.repo

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.baarton.runweather.Config
import com.baarton.runweather.ktor.WeatherApi
import com.baarton.runweather.models.SettingsViewModel.Companion.WEATHER_DATA_THRESHOLD_TAG
import com.baarton.runweather.models.weather.CurrentWeather
import com.baarton.runweather.sqldelight.DatabaseHelper
import com.russhwolf.settings.Settings
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

    fun getWeather(): CurrentWeather? {
        log.d("Get WeatherData from DB.")
        return dbHelper.getAll().let {
            if (it.isEmpty()) {
                null
            } else { //TODO consistency check on it.first() plus test(s)
                CurrentWeather(it.first(), getLastDownloadTime())
            }
        }
    }

    suspend fun refreshWeather(): CurrentWeather? {
        return if (isWeatherListStale()) {
            doRefreshWeather()
        } else {
            getWeather()
        }
    }

    private suspend fun doRefreshWeather(): CurrentWeather {
        val weatherResult = try {
            weatherApi.getJsonFromApi()
        } catch (e: Exception) {
            throw WeatherAPIException("Weather API has thrown an Exception: ${e.message}", e.cause)
        }
        log.d { "Weather network result: $weatherResult" }

        if (!weatherResult.isEmptyOrIncomplete()) {
            dbHelper.insert(weatherResult)
            val timeStamp = clock.now().toEpochMilliseconds()
            settings.putLong(DB_TIMESTAMP_KEY, timeStamp)
            log.d { "WeatherData result put to DB." }
            return CurrentWeather(weatherResult, timeStamp.milliseconds)
        } else {
            throw WeatherDataConsistencyException("Weather data obtained is empty or incomplete.-----\n$weatherResult\n-----")
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

    class WeatherAPIException(message: String, cause: Throwable?) : Exception(message, cause)
    class WeatherDataConsistencyException(message: String) : Exception(message)

}