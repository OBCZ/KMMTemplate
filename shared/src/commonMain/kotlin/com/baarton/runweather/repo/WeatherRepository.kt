package com.baarton.runweather.repo

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.baarton.runweather.Config
import com.baarton.runweather.ktor.ImageUrlBuilder
import com.baarton.runweather.ktor.WeatherDataApi
import com.baarton.runweather.sensor.location.Location
import com.baarton.runweather.model.viewmodel.SettingsViewModel.Companion.WEATHER_DATA_THRESHOLD_TAG
import com.baarton.runweather.model.weather.CurrentWeather
import com.baarton.runweather.sqldelight.DatabaseHelper
import com.russhwolf.settings.ObservableSettings
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class WeatherRepository(
    private val dbHelper: DatabaseHelper,
    private val settings: ObservableSettings,
    private val config: Config,
    private val weatherApi: WeatherDataApi,
    private val clock: Clock,
    private val log: Logger
) {

    companion object {
        internal const val DB_TIMESTAMP_KEY = "DbTimestampKey"

        fun getImageUrl(imageId: String): String {
            return ImageUrlBuilder.buildUrl(imageId)
        }
    }

    init {
        ensureNeverFrozen()
    }

    suspend fun refreshWeather(location: Location?): CurrentWeather? {
        return when {
            location == null -> {
                throw LocationConsistencyException("Location is null and wasn't probably initialized yet.")
            }
            isWeatherListStale() -> { doRefreshWeather(location) }
            else -> { getWeather() }
        }
    }

    private fun getWeather(): CurrentWeather? {
        log.d("Get WeatherData from DB.")
        return dbHelper.getAll().let {
            if (it.isEmpty()) {
                null
            } else {
                with(CurrentWeather(it.first(), getLastDownloadTime())) {
                    if (this.isInvalid()) {
                        throw WeatherDataConsistencyException("Weather data retrieved from the DB is empty or incomplete.-----\n${this.persistedWeather}\n-----")
                    } else {
                        this
                    }
                }
            }
        }
    }

    private suspend fun doRefreshWeather(location: Location): CurrentWeather {
        val weatherResult = try {
            weatherApi.getWeatherFromApi(location)
        } catch (e: Exception) {
            throw WeatherAPIException("Weather API has thrown an Exception: ${e.message}", e.cause)
        }
        log.d { "Weather network result: $weatherResult." }

        if (!weatherResult.isInvalid()) {
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
        val threshold = Duration.parseIsoString(
            settings.getString(WEATHER_DATA_THRESHOLD_TAG, config.weatherDataMinimumThreshold.toIsoString())
        )
        val now = clock.now().toEpochMilliseconds().milliseconds
        log.d {
            "Resolving staleness of data.\n" +
                "Saved data timestamp: $lastDownload\n" +
                "-------Timestamp now: $now\n" +
                "-----------Threshold: $threshold"
        }
        return (lastDownload + threshold < now).also {
            if (!it) {
                log.i { "Weather not fetched from network. Recently updated." }
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
    class LocationConsistencyException(message: String) : Exception(message)

}