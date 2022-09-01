package com.baarton.runweather.models

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.baarton.runweather.Config
import com.baarton.runweather.db.CurrentWeather
import com.baarton.runweather.ktor.WeatherApi
import com.baarton.runweather.models.SettingsViewModel.Companion.REFRESH_DURATION_TAG
import com.baarton.runweather.sqldelight.DatabaseHelper
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
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
        return dbHelper.getAll()
    }

    fun getLastDownloadTime(): Duration {
        return settings.getLong(DB_TIMESTAMP_KEY, 0).milliseconds
    }

    suspend fun refreshWeatherIfStale() {
        if (isWeatherListStale()) {
            refreshWeather()
        }
    }

    suspend fun refreshWeather() {
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
        val threshold = Duration.parseIsoString(settings.getString(REFRESH_DURATION_TAG, config.weatherDataMinimumThreshold.toIsoString()))
        val stale = lastDownload + threshold < clock.now().toEpochMilliseconds().milliseconds
        if (!stale) {
            log.i { "Weather not fetched from network. Recently updated" }
        }
        return stale
    }
}