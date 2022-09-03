package com.baarton.runweather

import app.cash.turbine.test
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import com.baarton.runweather.db.CurrentWeather
import com.baarton.runweather.mock.BRNO1
import com.baarton.runweather.mock.WeatherApiMock
import com.baarton.runweather.models.SettingsViewModel
import com.baarton.runweather.models.Weather
import com.baarton.runweather.models.WeatherData
import com.baarton.runweather.models.WeatherRepository
import com.baarton.runweather.sqldelight.DatabaseHelper
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
import kotlin.time.Duration.Companion.seconds


@RunWith(AndroidJUnit4::class)
class WeatherRepositoryTest {

    private var kermit = Logger(StaticConfig())
    private var testDbConnection = testDbConnection()
    private var dbHelper = DatabaseHelper(
        testDbConnection,
        kermit,
        Dispatchers.Default
    )
    private val settings = MockSettings()
    private val config = TestConfig
    private val apiMock = WeatherApiMock()

    // Need to start at non-zero time because the default value for db timestamp is 0
    private val clock = Clock.System

    private val repository: WeatherRepository =
        WeatherRepository(dbHelper, settings, config, apiMock, kermit, clock)

    @AfterTest
    fun tearDown() = runTest {
        apiMock.reset()
        testDbConnection.close()
    }

    @Test
    fun `Get weather without cache`() = runBlocking {
        apiMock.prepareResult(BRNO1.get())
        repository.refreshWeatherIfStale()
        repository.getWeather().test {
            assertEquals(
                CurrentWeather(
                    listOf(Weather("800", "Clear", "clear sky", "01d")),
                    "Brno",
                    WeatherData.MainData("265.90", "1021", "45"),
                    WeatherData.Wind("4.6", "345"),
                    null,
                    WeatherData.Sys("1646803774", "1646844989")
                ), awaitItem()
            )
        }
    }

    @Test
    fun `No web call if data is not stale`() = runTest {
        settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())
        apiMock.prepareResult(BRNO1.get())

        repository.refreshWeatherIfStale()
        assertEquals(0, apiMock.calledCount)
    }

    @Test
    fun `No web call if data is not stale and web call after delay`() = runTest {
        settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())
        apiMock.prepareResult(BRNO1.get())

        repository.refreshWeatherIfStale()
        assertEquals(0, apiMock.calledCount)

        runBlocking {
            delay(2000)
        }

        repository.refreshWeatherIfStale()
        assertEquals(1, apiMock.calledCount)
    }

    @Test
    fun `No web call if data is not stale and web call after delay with edited setting`() = runTest {
        settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())
        settings.putString(SettingsViewModel.REFRESH_DURATION_TAG, 5.seconds.toIsoString())
        apiMock.prepareResult(BRNO1.get())

        runBlocking {
            delay(2000)
        }

        repository.refreshWeatherIfStale()
        assertEquals(0, apiMock.calledCount)

        runBlocking {
            delay(3000)
        }

        repository.refreshWeatherIfStale()
        assertEquals(1, apiMock.calledCount)
    }

    @Test
    fun `Rethrow on API error`() = runTest {
        apiMock.throwOnCall(RuntimeException("Test error"))

        val throwable = assertFails {
            repository.refreshWeather()
        }
        assertEquals("Test error", throwable.message)
    }

    @Test
    fun `Rethrow on API error when stale`() = runTest {
        settings.putLong(
            WeatherRepository.DB_TIMESTAMP_KEY,
            (clock.now() - 2.hours).toEpochMilliseconds()
        )
        apiMock.throwOnCall(RuntimeException("Test error"))

        val throwable = assertFails {
            repository.refreshWeatherIfStale()
        }
        assertEquals("Test error", throwable.message)
    }

}