package com.baarton.runweather

import app.cash.turbine.FlowTurbine
import app.cash.turbine.test
import com.baarton.runweather.db.Breed
import com.baarton.runweather.mock.ClockMock
import com.baarton.runweather.mock.DogApiMock
import com.baarton.runweather.models.WeatherRepository
import com.baarton.runweather.models.WeatherViewModel
import com.baarton.runweather.models.WeatherViewState
import com.baarton.runweather.response.BreedResult
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
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
import kotlin.time.Duration.Companion.hours

class BreedViewModelTest {
    private var kermit = Logger(StaticConfig())
    private var testDbConnection = testDbConnection()
    private var dbHelper = DatabaseHelper(
        testDbConnection,
        kermit,
        Dispatchers.Default
    )
    private val settings = MockSettings()
    private val ktorApi = DogApiMock()

    // Need to start at non-zero time because the default value for db timestamp is 0
    private val clock = ClockMock(Clock.System.now())

    private val repository: WeatherRepository = WeatherRepository(dbHelper, settings, ktorApi, kermit, clock)
    private val viewModel by lazy { WeatherViewModel(repository, kermit) }

    companion object {
        private val appenzeller = Breed(1, "appenzeller", false)
        private val australianNoLike = Breed(2, "australian", false)
        private val australianLike = Breed(2, "australian", true)
        private val weatherViewStateSuccessNoFavorite = WeatherViewState(
            weather = listOf(appenzeller, australianNoLike)
        )
        private val weatherViewStateSuccessFavorite = WeatherViewState(
            weather = listOf(appenzeller, australianLike)
        )
        private val breedNames = weatherViewStateSuccessNoFavorite.weather?.map { it.name }.orEmpty()
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
    fun `Get breeds without cache`() = runBlocking {
        ktorApi.prepareResult(ktorApi.successResult())

        viewModel.weatherState.test {
            assertEquals(
                weatherViewStateSuccessNoFavorite,
                awaitItemPrecededBy(WeatherViewState(isLoading = true), WeatherViewState(isEmpty = true))
            )
        }
    }

    @Test
    fun `Get breeds empty`() = runBlocking {
        ktorApi.prepareResult(BreedResult(emptyMap(), "success"))

        viewModel.weatherState.test {
            assertEquals(
                WeatherViewState(isEmpty = true),
                awaitItemPrecededBy(WeatherViewState(isLoading = true))
            )
        }
    }

    @Test
    fun `Get updated breeds with cache and preserve favorites`() = runBlocking {
        settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, clock.currentInstant.toEpochMilliseconds())

        val successResult = ktorApi.successResult()
        val resultWithExtraBreed = successResult.copy(message = successResult.message + ("extra" to emptyList()))
        ktorApi.prepareResult(resultWithExtraBreed)

        dbHelper.insert(breedNames)
        dbHelper.updateFavorite(australianLike.id, true)

        viewModel.weatherState.test {
            assertEquals(weatherViewStateSuccessFavorite, awaitItemPrecededBy(WeatherViewState(isLoading = true)))
            expectNoEvents()

            viewModel.refreshWeather().join()
            // id is 5 here because it incremented twice when trying to insert duplicate breeds
            assertEquals(
                WeatherViewState(weatherViewStateSuccessFavorite.weather?.plus(Breed(5, "extra", false))),
                awaitItemPrecededBy(weatherViewStateSuccessFavorite.copy(isLoading = true))
            )
        }
    }

    @Test
    fun `Get updated breeds when stale and preserve favorites`() = runBlocking {
        settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, (clock.currentInstant - 2.hours).toEpochMilliseconds())

        val successResult = ktorApi.successResult()
        val resultWithExtraBreed = successResult.copy(message = successResult.message + ("extra" to emptyList()))
        ktorApi.prepareResult(resultWithExtraBreed)

        dbHelper.insert(breedNames)
        dbHelper.updateFavorite(australianLike.id, true)

        viewModel.weatherState.test {
            // id is 5 here because it incremented twice when trying to insert duplicate breeds
            assertEquals(
                WeatherViewState(weatherViewStateSuccessFavorite.weather?.plus(Breed(5, "extra", false))),
                awaitItemPrecededBy(WeatherViewState(isLoading = true), weatherViewStateSuccessFavorite)
            )
        }
    }

    @Test
    fun `Toggle favorite cached breed`() = runBlocking {
        settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, clock.currentInstant.toEpochMilliseconds())

        dbHelper.insert(breedNames)
        dbHelper.updateFavorite(australianLike.id, true)

        viewModel.weatherState.test {
            assertEquals(weatherViewStateSuccessFavorite, awaitItemPrecededBy(WeatherViewState(isLoading = true)))
            expectNoEvents()

            viewModel.updateBreedFavorite(australianLike).join()
            assertEquals(
                weatherViewStateSuccessNoFavorite,
                awaitItemPrecededBy(weatherViewStateSuccessFavorite.copy(isLoading = true))
            )
        }
    }

    @Test
    fun `No web call if data is not stale`() = runBlocking {
        settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, clock.currentInstant.toEpochMilliseconds())
        ktorApi.prepareResult(ktorApi.successResult())
        dbHelper.insert(breedNames)

        viewModel.weatherState.test {
            assertEquals(weatherViewStateSuccessNoFavorite, awaitItemPrecededBy(WeatherViewState(isLoading = true)))
            assertEquals(0, ktorApi.calledCount)
            expectNoEvents()

            viewModel.refreshWeather().join()
            assertEquals(
                weatherViewStateSuccessNoFavorite,
                awaitItemPrecededBy(weatherViewStateSuccessNoFavorite.copy(isLoading = true))
            )
            assertEquals(1, ktorApi.calledCount)
        }
    }

    @Test
    fun `Display API error on first run`() = runBlocking {
        ktorApi.throwOnCall(RuntimeException("Test error"))

        viewModel.weatherState.test {
            assertEquals(
                WeatherViewState(error = "Unable to download breed list"),
                awaitItemPrecededBy(WeatherViewState(isLoading = true), WeatherViewState(isEmpty = true))
            )
        }
    }

    @Test
    fun `Ignore API error with cache`() = runBlocking {
        dbHelper.insert(breedNames)
        settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, (clock.currentInstant - 2.hours).toEpochMilliseconds())
        ktorApi.throwOnCall(RuntimeException("Test error"))

        viewModel.weatherState.test {
            assertEquals(
                weatherViewStateSuccessNoFavorite,
                awaitItemPrecededBy(WeatherViewState(isLoading = true))
            )
            expectNoEvents()

            ktorApi.prepareResult(ktorApi.successResult())
            viewModel.refreshWeather().join()

            assertEquals(
                weatherViewStateSuccessNoFavorite,
                awaitItemPrecededBy(weatherViewStateSuccessNoFavorite.copy(isLoading = true))
            )
        }
    }

    @Test
    fun `Ignore API error on refresh with cache`() = runBlocking {
        ktorApi.prepareResult(ktorApi.successResult())

        viewModel.weatherState.test {
            assertEquals(
                weatherViewStateSuccessNoFavorite,
                awaitItemPrecededBy(WeatherViewState(isLoading = true), WeatherViewState(isEmpty = true))
            )
            expectNoEvents()

            ktorApi.throwOnCall(RuntimeException("Test error"))
            viewModel.refreshWeather().join()

            assertEquals(
                weatherViewStateSuccessNoFavorite,
                awaitItemPrecededBy(weatherViewStateSuccessNoFavorite.copy(isLoading = true))
            )
        }
    }

    @Test
    fun `Show API error on refresh without cache`() = runBlocking {
        settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, clock.currentInstant.toEpochMilliseconds())
        ktorApi.throwOnCall(RuntimeException("Test error"))

        viewModel.weatherState.test {
            assertEquals(WeatherViewState(isEmpty = true), awaitItemPrecededBy(WeatherViewState(isLoading = true)))
            expectNoEvents()

            viewModel.refreshWeather().join()
            assertEquals(
                WeatherViewState(error = "Unable to refresh breed list"),
                awaitItemPrecededBy(WeatherViewState(isEmpty = true, isLoading = true))
            )
        }
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
