package com.baarton.runweather.models

import co.touchlab.kermit.Logger
import com.baarton.runweather.Config
import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.models.weather.CurrentWeather
import com.baarton.runweather.repo.WeatherRepository
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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class WeatherViewModel(
    private val config: Config,
    private val weatherRepository: WeatherRepository,
    private val clock: Clock,
    log: Logger
) : ViewModel() {
    private val log = log.withTag("WeatherViewModel")

    private var isClosed = false
    private val pollingDispatcher: CoroutineDispatcher = Dispatchers.Default

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
        val refreshFlow = channelFlow<Throwable?> {
            while (!isClosed) {
                log.i("Get WeatherData for poll send.")
                mutableWeatherState.update {
                    it.copy(isLoading = true).also {
                        log.d { "State updated with $it" }
                    }
                }
                try {
                    log.i("Try to refresh WeatherData.")
                    weatherRepository.refreshWeather()
                    send(null)
                } catch (exception: Exception) {
                    send(exception)
                }
                log.i("Delaying next poll.")
                delay(config.weatherDataRequestInterval)
            }
        }.flowOn(pollingDispatcher)
            .cancellable() //TODO check when polling closes (phone lock, etc) and its config in general

        viewModelScope.launch {
            log.d { "WeatherData refresh coroutine launch." }
            combine(
                refreshFlow,
                weatherRepository.getWeather()
            ) { throwable, weather -> throwable to weather }
                .collect { (error, weather) ->
                    log.d("Weather collected.")
                    mutableWeatherState.update { previousState ->
                        val errorMessage = if (error != null) {
                            "Unable to download weather list."
                        } else {
                            previousState.error
                        }

                        if (shouldUpdateState(previousState, weather) || error != null) {
                            WeatherViewState(
                                isLoading = false,
                                lastUpdated = weather?.let { timeStampDuration(it.timestamp) },
                                weather = weather?.persistedWeather,
                                error = errorMessage.takeIf { weather == null }
                            ).also {
                                log.d { "Updating weather state with $it." }
                            }
                        } else {
                            previousState.also {
                                log.d { "Weather state not updated." }
                            }
                        }
                    }
                }
        }
    }

    private fun shouldUpdateState(
        previousState: WeatherViewState,
        weather: CurrentWeather?
    ): Boolean {
        return previousState.weather != weather?.persistedWeather ||
            previousState.lastUpdated != weather?.timestamp?.let { timeStampDuration(it) }
    }

    fun refreshWeather(): Job {
        mutableWeatherState.update {
            it.copy(isLoading = true).also {
                log.d { "State updated with $it" }
            }
        }
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
            if (it.weather == null) {
                WeatherViewState(error = "Unable to refresh weather list")
            } else {
                // Just let it fail silently if we have a cache
                it.copy(isLoading = false)
            }.also {
                log.d { "State updated with $it" }
            }
        }
    }

    private fun timeStampDuration(dataTimestamp: Duration): Duration {
        return clock.now().toEpochMilliseconds().milliseconds - dataTimestamp
    }
}

//TODO probably needs to be moved somewhere - UIUtils in common module?
fun lastUpdatedResId(timestampAge: Duration?): Pair<StringResource, Long?> {
    return when (timestampAge?.inWholeSeconds) {
        null -> SharedRes.strings.app_n_a to null
        0L -> SharedRes.strings.fragment_weather_last_updated_now to null
        in 1L..59L -> SharedRes.strings.fragment_weather_last_updated_sec_time to timestampAge.inWholeSeconds
        else -> SharedRes.strings.fragment_weather_last_updated_min_time to timestampAge.inWholeMinutes
    }
}

data class WeatherViewState(
    val weather: PersistedWeather? = null,
    val lastUpdated: Duration? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)