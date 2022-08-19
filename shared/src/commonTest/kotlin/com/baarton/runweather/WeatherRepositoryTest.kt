package com.baarton.runweather

import app.cash.turbine.test
import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import com.baarton.runweather.db.CurrentWeather
import com.baarton.runweather.mock.BRNO1
import com.baarton.runweather.mock.ClockMock
import com.baarton.runweather.mock.WeatherApiMock
import com.baarton.runweather.models.Weather
import com.baarton.runweather.models.WeatherData
import com.baarton.runweather.models.WeatherRepository
import com.russhwolf.settings.MockSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.time.Duration.Companion.hours

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
    private val ktorApi = WeatherApiMock()

    // Need to start at non-zero time because the default value for db timestamp is 0
    private val clock = ClockMock(Clock.System.now())

    private val repository: WeatherRepository =
        WeatherRepository(dbHelper, settings, ktorApi, kermit, clock)

    companion object {
        // private val appenzeller = Breed(1, "appenzeller", false)
        // private val australianNoLike = Breed(2, "australian", false)
        // private val australianLike = Breed(2, "australian", true)
        // private val breedsNoFavorite = listOf(appenzeller, australianNoLike)
        // private val breedsFavorite = listOf(appenzeller, australianLike)
        // private val breedNames = breedsFavorite.map { it.name }
    }

    @AfterTest
    fun tearDown() = runTest {
        testDbConnection.close()
    }

    @Test
    fun `Get weather without cache`() = runBlocking {
        ktorApi.prepareResult(BRNO1.get())
        repository.refreshWeatherIfStale()
        repository.getWeather().test {
            assertEquals(
                listOf(
                    CurrentWeather(
                        listOf(Weather("800", "Clear", "clear sky", "01d")),
                        "Brno",
                        WeatherData.MainData("265.90", "1021", "45"),
                        WeatherData.Wind("4.6", "345"),
                        null,
                        WeatherData.Sys("1646803774", "1646844989")
                    )
                ), awaitItem()
            )
        }
    }

    // @Test
    // fun `Get updated breeds with cache and preserve favorites`() = runBlocking {
    //     val successResult = ktorApi.brno()
    //     val resultWithExtraBreed = successResult.copy(message = successResult.message + ("extra" to emptyList()))
    //     ktorApi.prepareResult(resultWithExtraBreed)
    //
    //     dbHelper.insert(breedNames)
    //     dbHelper.updateFavorite(australianLike.id, true)
    //
    //     repository.getWeather().test {
    //         assertEquals(breedsFavorite, awaitItem())
    //         expectNoEvents()
    //
    //         repository.refreshWeather()
    //         // id is 5 here because it incremented twice when trying to insert duplicate breeds
    //         assertEquals(breedsFavorite + Breed(5, "extra", false), awaitItem())
    //     }
    // }

    // @Test
    // fun `Get updated breeds when stale and preserve favorites`() = runBlocking {
    //     settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, (clock.currentInstant - 2.hours).toEpochMilliseconds())
    //
    //     val successResult = ktorApi.brno()
    //     val resultWithExtraBreed = successResult.copy(message = successResult.message + ("extra" to emptyList()))
    //     ktorApi.prepareResult(resultWithExtraBreed)
    //
    //     dbHelper.insert(breedNames)
    //     dbHelper.updateFavorite(australianLike.id, true)
    //
    //     repository.refreshWeatherIfStale()
    //     repository.getWeather().test {
    //         // id is 5 here because it incremented twice when trying to insert duplicate breeds
    //         assertEquals(breedsFavorite + Breed(5, "extra", false), awaitItem())
    //     }
    // }

    // @Test
    // fun `Toggle favorite cached breed`() = runBlocking {
    //     dbHelper.insert(breedNames)
    //     dbHelper.updateFavorite(australianLike.id, true)
    //
    //     repository.getWeather().test {
    //         assertEquals(breedsFavorite, awaitItem())
    //         expectNoEvents()
    //
    //         repository.updateBreedFavorite(australianLike)
    //         assertEquals(breedsNoFavorite, awaitItem())
    //     }
    // }

    @Test
    fun `No web call if data is not stale`() = runTest {
        settings.putLong(
            WeatherRepository.DB_TIMESTAMP_KEY,
            clock.currentInstant.toEpochMilliseconds()
        )
        ktorApi.prepareResult(BRNO1.get())

        repository.refreshWeatherIfStale()
        assertEquals(0, ktorApi.calledCount)

        repository.refreshWeather()
        assertEquals(1, ktorApi.calledCount)
    }

    @Test
    fun `Rethrow on API error`() = runTest {
        ktorApi.throwOnCall(RuntimeException("Test error"))

        val throwable = assertFails {
            repository.refreshWeather()
        }
        assertEquals("Test error", throwable.message)
    }

    @Test
    fun `Rethrow on API error when stale`() = runTest {
        settings.putLong(
            WeatherRepository.DB_TIMESTAMP_KEY,
            (clock.currentInstant - 2.hours).toEpochMilliseconds()
        )
        ktorApi.throwOnCall(RuntimeException("Test error"))

        val throwable = assertFails {
            repository.refreshWeatherIfStale()
        }
        assertEquals("Test error", throwable.message)
    }
}
