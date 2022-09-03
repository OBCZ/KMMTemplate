package com.baarton.runweather

import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import com.baarton.runweather.db.CurrentWeather
import com.baarton.runweather.mock.BRNO1
import com.baarton.runweather.mock.BRNO2
import com.baarton.runweather.mock.ClockMock
import com.baarton.runweather.mock.EMPTY
import com.baarton.runweather.mock.WeatherApiMock
import com.baarton.runweather.models.Weather
import com.baarton.runweather.models.WeatherData
import com.baarton.runweather.models.WeatherRepository
import com.baarton.runweather.models.WeatherViewModel
import com.baarton.runweather.models.WeatherViewState
import com.baarton.runweather.sqldelight.DatabaseHelper
import com.russhwolf.settings.MockSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours


//TODO tests with weather data thresholds from Config
class WeatherViewModelTest {

    private var kermit = Logger(StaticConfig())
    private var testDbConnection = testDbConnection()
    private var dbHelper = DatabaseHelper(testDbConnection, kermit, Dispatchers.Default)
    private val settings = MockSettings()
    private val config = TestConfig
    private val apiMock = WeatherApiMock()
    private val clock = ClockMock(Clock.System.now()) //TODO consider using real clock also here
    private val repository: WeatherRepository = WeatherRepository(dbHelper, settings, config, apiMock, kermit, clock)
    private val testConfig: Config = TestConfig

    private val viewModel by lazy { WeatherViewModel(testConfig, repository, clock, kermit) }

    companion object {

        private val weatherSuccessStateBrno1 = WeatherViewState(
            weather = CurrentWeather(
                listOf(Weather("800", "Clear", "clear sky", "01d")),
                "Brno",
                WeatherData.MainData("265.90", "1021", "45"),
                WeatherData.Wind("4.6", "345"),
                null,
                WeatherData.Sys("1646803774", "1646844989")
            )
        )

        private val weatherSuccessStateBrno2 = WeatherViewState(
            weather = CurrentWeather(
                listOf(Weather("800", "Clear", "clear sky", "01d")),
                "Brno",
                WeatherData.MainData("260.90", "1025", "55"),
                WeatherData.Wind("4.7", "355"),
                null,
                WeatherData.Sys("1646806774", "1646842989")
            )
        )
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        apiMock.reset()
        testDbConnection.close()
    }

    @Test
    fun `Get weather empty`() = runBlocking {
        apiMock.prepareResult(EMPTY.get())

        viewModel.weatherState.test {
            assertEquals(
                WeatherViewState(isEmpty = true),
                awaitItemPrecededBy(WeatherViewState(isLoading = true))
            )
        }
    }

    @Test
    fun `Get weather without cache`() = runBlocking {
        apiMock.prepareResult(BRNO1.get())

        viewModel.weatherState.test {
            assertEquals(
                weatherSuccessStateBrno1,
                awaitItemPrecededBy(
                    WeatherViewState(isLoading = true),
                    WeatherViewState(isEmpty = true)
                )
            )
        }
    }

    @Test
    fun `Get weather with cache and update from network call`() = runBlocking {
        apiMock.prepareResult(listOf(BRNO2.get()))
        setDataAge(clock.currentInstant)
        dbHelper.insert(BRNO1.get())

        assertEquals(0, apiMock.calledCount)

        viewModel.weatherState.test(2000) {
            assertEquals(
                weatherSuccessStateBrno1,
                awaitItemPrecededBy(WeatherViewState(isLoading = true))
            )
            assertEquals(0, apiMock.calledCount)
            expectNoEvents()

            setDataAge(clock.currentInstant - 2.hours)

            assertEquals(
                weatherSuccessStateBrno2,
                awaitItemPrecededBy(weatherSuccessStateBrno1.copy(isLoading = true))
            )
            assertEquals(1, apiMock.calledCount)
        }
    }

    @Test
    fun `Get weather via poll - updated from network call`() = runBlocking {
        apiMock.prepareResult(listOf(BRNO1.get(), BRNO2.get()))

        viewModel.weatherState.test(2000) {
            assertEquals(
                weatherSuccessStateBrno1,
                awaitItemPrecededBy(
                    WeatherViewState(isLoading = true),
                    WeatherViewState(isEmpty = true)
                )
            )
            assertEquals(1, apiMock.calledCount)

            setDataAge(clock.currentInstant - 2.hours)

            assertEquals(
                weatherSuccessStateBrno2,
                awaitItemPrecededBy(weatherSuccessStateBrno1)
            )
            assertEquals(2, apiMock.calledCount)
        }
    }

    @Test
    fun `Get weather via poll - not updated from network call`() = runBlocking {
        apiMock.prepareResult(listOf(BRNO1.get(), BRNO2.get()))

        viewModel.weatherState.test(2000) {
            assertEquals(
                weatherSuccessStateBrno1,
                awaitItemPrecededBy(
                    WeatherViewState(isLoading = true),
                    WeatherViewState(isEmpty = true)
                )
            )
            assertEquals(1, apiMock.calledCount)
            expectNoEvents()
        }
    }

    @Test
    fun `Display API error on first run`() = runBlocking {
        setDataAge(clock.currentInstant - 2.hours)
        apiMock.throwOnCall(RuntimeException("Test error"))

        viewModel.weatherState.test {
            assertEquals(
                WeatherViewState(lastUpdated = 2.hours, error = "Unable to download weather list."),
                awaitItemPrecededBy(WeatherViewState(isLoading = true), WeatherViewState(isEmpty = true))
            )
        }
    }

    @Test
    fun `Ignore API error with cache`() = runBlocking {
        dbHelper.insert(BRNO1.get())
        setDataAge(clock.currentInstant - 2.hours)
        apiMock.throwOnCall(RuntimeException("Test error"))

        viewModel.weatherState.test(2000) {
            assertEquals(
                weatherSuccessStateBrno1.copy(lastUpdated = 2.hours),
                awaitItemPrecededBy(WeatherViewState(isLoading = true))
            )
            expectNoEvents()

            apiMock.prepareResult(BRNO2.get())

            assertEquals(
                weatherSuccessStateBrno2,
                awaitItemPrecededBy(weatherSuccessStateBrno1)
            )
        }
    }

    @Test
    fun `Ignore API error on refresh with cache`() = runBlocking {
        apiMock.prepareResult(BRNO1.get())

        viewModel.weatherState.test(2000) {
            assertEquals(
                weatherSuccessStateBrno1,
                awaitItemPrecededBy(WeatherViewState(isLoading = true), WeatherViewState(isEmpty = true))
            )
            expectNoEvents()

            apiMock.throwOnCall(RuntimeException("Test error"))
            viewModel.refreshWeather().join()

            assertEquals(
                weatherSuccessStateBrno1,
                awaitItemPrecededBy(weatherSuccessStateBrno1.copy(isLoading = true))
            )
        }
    }

    @Test
    fun `Show API error on refresh without cache`() = runBlocking {
        setDataAge(clock.currentInstant)
        apiMock.throwOnCall(RuntimeException("Test error"))

        viewModel.weatherState.test {
            assertEquals(WeatherViewState(isEmpty = true), awaitItemPrecededBy(WeatherViewState(isLoading = true)))
            expectNoEvents()

            viewModel.refreshWeather().join()
            assertEquals(
                WeatherViewState(error = "Unable to refresh weather list"),
                awaitItemPrecededBy(WeatherViewState(isEmpty = true, isLoading = true))
            )
        }
    }

    private fun setDataAge(instant: Instant) {
        settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, instant.toEpochMilliseconds())
    }

}

// There's a race condition where intermediate states can get missed if the next state comes too fast.
// This function addresses that by awaiting an item that may or may not be preceded by the specified other items
private suspend fun FlowTurbine<WeatherViewState>.awaitItemPrecededBy(vararg items: WeatherViewState): WeatherViewState {
    var nextItem = awaitItem()
    for (item in items) {
        if (item == nextItem) {
            nextItem = awaitItem()
        }
    }
    return nextItem
}