package com.baarton.runweather.repo

import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import com.baarton.runweather.AndroidJUnit4
import com.baarton.runweather.RunWith
import com.baarton.runweather.TestConfig
import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.mock.BRNO1
import com.baarton.runweather.mock.WeatherApiMock
import com.baarton.runweather.models.SettingsViewModel
import com.baarton.runweather.models.weather.Weather
import com.baarton.runweather.models.weather.WeatherData
import com.baarton.runweather.repo.WeatherRepository.Companion.DB_TIMESTAMP_KEY
import com.baarton.runweather.sqldelight.DatabaseHelper
import com.baarton.runweather.testDbConnection
import com.russhwolf.settings.MockSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


@RunWith(AndroidJUnit4::class)
class WeatherRepositoryTest {

    private var logger = Logger(StaticConfig())
    private var testDbConnection = testDbConnection()
    private var dbHelper = DatabaseHelper(testDbConnection, logger, Dispatchers.Default)

    private val settingsMock = MockSettings()
    private val testConfig = TestConfig
    private val apiMock = WeatherApiMock()
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
                    listOf(Weather("800", "Clear", "clear sky", "01d")),
                    "Brno1",
                    WeatherData.MainData("265.90", "1021", "45"),
                    WeatherData.Wind("4.6", "345"),
                    null,
                    WeatherData.Sys("1646803774", "1646844989")
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