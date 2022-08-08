package com.baarton.runweather.models

import co.touchlab.kermit.Logger
import com.baarton.runweather.db.CurrentWeather
import com.baarton.runweather.res.SharedRes
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val clock: Clock,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("WeatherViewModel")

    private val pollingDispatcher: CoroutineDispatcher = Dispatchers.Default/*Executors.newFixedThreadPool(5).asCoroutineDispatcher()*/
    private var isClosed = false

    private val mutableWeatherState: MutableStateFlow<WeatherViewState> =
        MutableStateFlow(WeatherViewState(isLoading = true))

    val weatherState: StateFlow<WeatherViewState> = mutableWeatherState

    init {
        observeWeather()
    }


    override fun onCleared() {
        log.i("Close polling.")
        isClosed = true
        pollingDispatcher.cancel()
        log.v("Clearing WeatherViewModel")
    }

    private fun observeWeather() {
        // Refresh breeds, and emit any exception that was thrown so we can handle it downstream
        val refreshFlow = channelFlow<Throwable?> {
            while (!isClosed) {
                log.i("Get WeatherData for poll send.")
                try {
                    log.i("Try to refresh WeatherData.")
                    weatherRepository.refreshWeatherIfStale()
                    send(null)
                } catch (exception: Exception) {
                    send(exception)
                }
                log.i("Delaying next poll.")

                delay(5000)
            }
        }.flowOn(pollingDispatcher).cancellable()
        //TODO check when polling closes (phone lock, etc) and its config in general

        viewModelScope.launch {
            log.d { "WeatherData refresh coroutine launch." }

            combine(refreshFlow, weatherRepository.getWeather()) { throwable, weather -> throwable to weather }
                .collect { (error, weather) ->
                    log.d("Weather collected")
                    mutableWeatherState.update { previousState ->
                        log.d { "Updating weather state." }

                        val errorMessage = if (error != null) {
                            "Unable to download weather list"
                        } else {
                            previousState.error
                        }
                        WeatherViewState(
                            isLoading = false,
                            lastUpdated = timeStampMins(weatherRepository.getLastDownloadTime()),
                            weather = weather.takeIf { it.isNotEmpty() },
                            error = errorMessage.takeIf { weather.isEmpty() },
                            isEmpty = weather.isEmpty() && errorMessage == null
                        )
                    }
                }
        }
    }

    fun refreshWeather(): Job {
        // Set loading state, which will be cleared when the repository re-emits
        mutableWeatherState.update { it.copy(isLoading = true) }
        return viewModelScope.launch {
            log.v { "refreshWeather" }
            try {
                weatherRepository.refreshWeather()
            } catch (exception: Exception) {
                handleWeatherError(exception)
            }
        }
    }

    private fun handleWeatherError(throwable: Throwable) {
        log.e(throwable) { "Error downloading weather list" }
        mutableWeatherState.update {
            if (it.weather.isNullOrEmpty()) {
                WeatherViewState(error = "Unable to refresh weather list")
            } else {
                // Just let it fail silently if we have a cache
                it.copy(isLoading = false)
            }
        }
    }

    private fun timeStampMins(dataTimestamp: Long): Long {
        val diff = clock.now() - Instant.fromEpochMilliseconds(dataTimestamp)
        return diff.inWholeMinutes
    }

}

//TODO probably needs to be moved somewhere - UIUtils in common module?
fun lastUpdatedResId(timestampAge: Long): Pair<StringResource, Long?> {
    return when(timestampAge) {
        0L -> SharedRes.strings.fragment_weather_last_updated_now to null
        else -> SharedRes.strings.fragment_weather_last_updated_time to timestampAge
    }
}

data class WeatherViewState(
    val weather: List<CurrentWeather>? = null,
    val lastUpdated: Long = 0L,
    val error: String? = null,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)
