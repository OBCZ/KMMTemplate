package com.baarton.runweather.repo

import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import com.baarton.runweather.TestConfig
import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.mock.BRNO1
import com.baarton.runweather.mock.WeatherDataApiMock
import com.baarton.runweather.model.Angle.Companion.deg
import com.baarton.runweather.model.Humidity.Companion.percent
import com.baarton.runweather.model.Pressure.Companion.hpa
import com.baarton.runweather.model.Temperature.Companion.kelvin
import com.baarton.runweather.model.Velocity.Companion.mps
import com.baarton.runweather.model.viewmodel.SettingsViewModel
import com.baarton.runweather.model.weather.Weather
import com.baarton.runweather.model.weather.WeatherData
import com.baarton.runweather.model.weather.WeatherId
import com.baarton.runweather.repo.WeatherRepository.Companion.DB_TIMESTAMP_KEY
import com.baarton.runweather.sqldelight.DatabaseHelper
import com.baarton.runweather.testDbConnection
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


class WeatherRepositoryTest {

    private var logger = Logger(StaticConfig())
    private var testDbConnection = testDbConnection()
    private var dbHelper = DatabaseHelper(testDbConnection, logger, Dispatchers.Default)

    private val settingsMock = MapSettings()
    private val testConfig = TestConfig
    private val apiMock = WeatherDataApiMock()
    private val clock = Clock.System

    private val repository: WeatherRepository = WeatherRepository(dbHelper, settingsMock, testConfig, apiMock, logger, clock)

    @AfterTest
    fun tearDown() = runTest {
        apiMock.reset()
        testDbConnection.close()
    }

    @Test
    fun `Web call with no data`() = runBlocking {
        apiMock.prepareResults(BRNO1.data)
        repository.refreshWeather().let {
            assertEquals(
                PersistedWeather(
                    listOf(Weather(WeatherId.CLEAR_SKY, "Clear", "clear sky", "01d")),
                    "Brno1",
                    WeatherData.MainData(265.90.kelvin, 1021.hpa, 45.percent),
                    WeatherData.Wind(4.6.mps, 345.deg),
                    WeatherData.Rain(),
                    WeatherData.Sys(Instant.fromEpochSeconds(1646803774), Instant.fromEpochSeconds(1646844989))
                ), it?.persistedWeather
            )
            assertEquals(
                settingsMock.getLong(DB_TIMESTAMP_KEY, 0).milliseconds, it?.timestamp
            )
        }
    }

    @Test
    fun `No web call if data is not stale`() = runTest {
        settingsMock.putLong(DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())
        apiMock.prepareResults(BRNO1.data)

        repository.refreshWeather()
        assertEquals(0, apiMock.calledCount)
    }

    @Test
    fun `No web call if data is not stale and web call after delay`() = runTest {
        settingsMock.putLong(DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())
        apiMock.prepareResults(BRNO1.data)

        repository.refreshWeather()
        assertEquals(0, apiMock.calledCount)

        runBlocking {
            delay(2000)
        }

        repository.refreshWeather()
        assertEquals(1, apiMock.calledCount)
    }

    @Test
    fun `No web call if data is not stale and web call after delay - edited setting 1`() = runTest {
        settingsMock.putLong(DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())
        settingsMock.putString(
            SettingsViewModel.WEATHER_DATA_THRESHOLD_TAG,
            5.seconds.toIsoString()
        )
        apiMock.prepareResults(BRNO1.data)

        runBlocking {
            delay(2000)
        }

        repository.refreshWeather()
        assertEquals(0, apiMock.calledCount)

        runBlocking {
            delay(3000)
        }

        repository.refreshWeather()
        assertEquals(1, apiMock.calledCount)
    }

    @Test
    fun `No web call if data is not stale and web call after delay - edited setting 2`() = runTest {
        settingsMock.putLong(DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())
        settingsMock.putString(
            SettingsViewModel.WEATHER_DATA_THRESHOLD_TAG,
            5.seconds.toIsoString()
        )
        apiMock.prepareResults(BRNO1.data)

        runBlocking {
            delay(1000)
        }

        repository.refreshWeather()
        assertEquals(0, apiMock.calledCount)

        runBlocking {
            delay(1000)
        }

        repository.refreshWeather()
        assertEquals(0, apiMock.calledCount)

        runBlocking {
            delay(3000)
        }

        repository.refreshWeather()
        assertEquals(1, apiMock.calledCount)
    }

    @Test
    fun `Rethrow on API error`() = runTest {
        apiMock.prepareResults(RuntimeException("Test error"))

        val throwable = assertFails {
            repository.refreshWeather()
        }
        assertEquals("Weather API has thrown an Exception: Test error", throwable.message)
    }

    @Test
    fun `Rethrow on API error when stale`() = runTest {
        settingsMock.putLong(DB_TIMESTAMP_KEY, (Clock.System.now() - 2.hours).toEpochMilliseconds())
        apiMock.prepareResults(RuntimeException("Test error"))

        val throwable = assertFails {
            repository.refreshWeather()
        }
        assertEquals("Weather API has thrown an Exception: Test error", throwable.message)
    }

}