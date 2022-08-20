package com.baarton.runweather

import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import com.baarton.runweather.db.CurrentWeather
import com.baarton.runweather.mock.BRNO1
import com.baarton.runweather.mock.ClockMock
import com.baarton.runweather.mock.EMPTY
import com.baarton.runweather.mock.WeatherApiMock
import com.baarton.runweather.models.Weather
import com.baarton.runweather.models.WeatherData
import com.baarton.runweather.models.WeatherRepository
import com.baarton.runweather.models.WeatherViewModel
import com.baarton.runweather.models.WeatherViewState
import com.russhwolf.settings.MockSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherViewModelTest {
    private var kermit = Logger(StaticConfig())
    private var testDbConnection = testDbConnection()
    private var dbHelper = DatabaseHelper(
        testDbConnection,
        kermit,
        Dispatchers.Default
    )
    private val settings = MockSettings()
    private val apiMock = WeatherApiMock()

    // Need to start at non-zero time because the default value for db timestamp is 0
    private val clock = ClockMock(Clock.System.now())

    private val repository: WeatherRepository = WeatherRepository(dbHelper, settings, apiMock, kermit, clock)
    private val viewModel by lazy { WeatherViewModel(repository, clock, kermit) }



    companion object {

        // private val australianNoLike = CurrentWeather(2, "australian", false)
        // private val australianLike = CurrentWeather(2, "australian", true)
        private val weatherViewSuccessState = WeatherViewState(
            weather = listOf(
                CurrentWeather(
                    listOf(Weather("800", "Clear", "clear sky", "01d")),
                    "Brno",
                    WeatherData.MainData("265.90", "1021", "45"),
                    WeatherData.Wind("4.6", "345"),
                    null,
                    WeatherData.Sys("1646803774", "1646844989")
                )
            )
        )
        // private val weatherViewStateSuccessFavorite = WeatherViewState(
        //     weather = listOf(brno, australianLike)
        // )

        // private val breedNames = weatherViewSuccessState.weather?.get(0). { it.name }.orEmpty()
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
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
                weatherViewSuccessState,
                awaitItemPrecededBy(WeatherViewState(isLoading = true), WeatherViewState(isEmpty = true))
            )
        }
    }

    //FIXME get other tests working
    ////
    // @Test
    // fun `Get updated breeds with cache and preserve favorites`() = runBlocking {
    //     settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, clock.currentInstant.toEpochMilliseconds())
    //
    //     val successResult = apiMock.brno()
    //     val resultWithExtraBreed = successResult.copy(message = successResult.message + ("extra" to emptyList()))
    //     apiMock.prepareResult(resultWithExtraBreed)
    //
    //     dbHelper.insert(breedNames)
    //     // dbHelper.updateFavorite(australianLike.id, true)
    //
    //     viewModel.weatherState.test {
    //         assertEquals(weatherViewStateSuccessFavorite, awaitItemPrecededBy(WeatherViewState(isLoading = true)))
    //         expectNoEvents()
    //
    //         viewModel.refreshWeather().join()
    //         // id is 5 here because it incremented twice when trying to insert duplicate breeds
    //         assertEquals(
    //             WeatherViewState(weatherViewStateSuccessFavorite.weather?.plus(CurrentWeather(5, "extra", false))),
    //             awaitItemPrecededBy(weatherViewStateSuccessFavorite.copy(isLoading = true))
    //         )
    //     }
    // }
    //
    // @Test
    // fun `Get updated breeds when stale and preserve favorites`() = runBlocking {
    //     settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, (clock.currentInstant - 2.hours).toEpochMilliseconds())
    //
    //     val successResult = apiMock.brno()
    //     val resultWithExtraBreed = successResult.copy(message = successResult.message + ("extra" to emptyList()))
    //     apiMock.prepareResult(resultWithExtraBreed)
    //
    //     dbHelper.insert(breedNames)
    //     dbHelper.updateFavorite(australianLike.id, true)
    //
    //     viewModel.weatherState.test {
    //         // id is 5 here because it incremented twice when trying to insert duplicate breeds
    //         assertEquals(
    //             WeatherViewState(weatherViewStateSuccessFavorite.weather?.plus(CurrentWeather(5, "extra", false))),
    //             awaitItemPrecededBy(WeatherViewState(isLoading = true), weatherViewStateSuccessFavorite)
    //         )
    //     }
    // }
    //
    // // @Test
    // // fun `Toggle favorite cached breed`() = runBlocking {
    // //     settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, clock.currentInstant.toEpochMilliseconds())
    // //
    // //     dbHelper.insert(breedNames)
    // //     dbHelper.updateFavorite(australianLike.id, true)
    // //
    // //     viewModel.weatherState.test {
    // //         assertEquals(weatherViewStateSuccessFavorite, awaitItemPrecededBy(WeatherViewState(isLoading = true)))
    // //         expectNoEvents()
    // //
    // //         viewModel.updateBreedFavorite(australianLike).join()
    // //         assertEquals(
    // //             weatherViewSuccessState,
    // //             awaitItemPrecededBy(weatherViewStateSuccessFavorite.copy(isLoading = true))
    // //         )
    // //     }
    // // }
    //
    // @Test
    // fun `No web call if data is not stale`() = runBlocking {
    //     settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, clock.currentInstant.toEpochMilliseconds())
    //     apiMock.prepareResult(apiMock.brno())
    //     dbHelper.insert(breedNames)
    //
    //     viewModel.weatherState.test {
    //         assertEquals(weatherViewSuccessState, awaitItemPrecededBy(WeatherViewState(isLoading = true)))
    //         assertEquals(0, apiMock.calledCount)
    //         expectNoEvents()
    //
    //         viewModel.refreshWeather().join()
    //         assertEquals(
    //             weatherViewSuccessState,
    //             awaitItemPrecededBy(weatherViewSuccessState.copy(isLoading = true))
    //         )
    //         assertEquals(1, apiMock.calledCount)
    //     }
    // }
    //
    // @Test
    // fun `Display API error on first run`() = runBlocking {
    //     apiMock.throwOnCall(RuntimeException("Test error"))
    //
    //     viewModel.weatherState.test {
    //         assertEquals(
    //             WeatherViewState(error = "Unable to download breed list"),
    //             awaitItemPrecededBy(WeatherViewState(isLoading = true), WeatherViewState(isEmpty = true))
    //         )
    //     }
    // }
    //
    // @Test
    // fun `Ignore API error with cache`() = runBlocking {
    //     dbHelper.insert(breedNames)
    //     settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, (clock.currentInstant - 2.hours).toEpochMilliseconds())
    //     apiMock.throwOnCall(RuntimeException("Test error"))
    //
    //     viewModel.weatherState.test {
    //         assertEquals(
    //             weatherViewSuccessState,
    //             awaitItemPrecededBy(WeatherViewState(isLoading = true))
    //         )
    //         expectNoEvents()
    //
    //         apiMock.prepareResult(apiMock.brno())
    //         viewModel.refreshWeather().join()
    //
    //         assertEquals(
    //             weatherViewSuccessState,
    //             awaitItemPrecededBy(weatherViewSuccessState.copy(isLoading = true))
    //         )
    //     }
    // }
    //
    // @Test
    // fun `Ignore API error on refresh with cache`() = runBlocking {
    //     apiMock.prepareResult(apiMock.brno())
    //
    //     viewModel.weatherState.test {
    //         assertEquals(
    //             weatherViewSuccessState,
    //             awaitItemPrecededBy(WeatherViewState(isLoading = true), WeatherViewState(isEmpty = true))
    //         )
    //         expectNoEvents()
    //
    //         apiMock.throwOnCall(RuntimeException("Test error"))
    //         viewModel.refreshWeather().join()
    //
    //         assertEquals(
    //             weatherViewSuccessState,
    //             awaitItemPrecededBy(weatherViewSuccessState.copy(isLoading = true))
    //         )
    //     }
    // }
    //
    // @Test
    // fun `Show API error on refresh without cache`() = runBlocking {
    //     settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, clock.currentInstant.toEpochMilliseconds())
    //     apiMock.throwOnCall(RuntimeException("Test error"))
    //
    //     viewModel.weatherState.test {
    //         assertEquals(WeatherViewState(isEmpty = true), awaitItemPrecededBy(WeatherViewState(isLoading = true)))
    //         expectNoEvents()
    //
    //         viewModel.refreshWeather().join()
    //         assertEquals(
    //             WeatherViewState(error = "Unable to refresh breed list"),
    //             awaitItemPrecededBy(WeatherViewState(isEmpty = true, isLoading = true))
    //         )
    //     }
    // }
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
