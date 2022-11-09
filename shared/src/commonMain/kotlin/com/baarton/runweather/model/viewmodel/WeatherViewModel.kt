package com.baarton.runweather.model.viewmodel

import co.touchlab.kermit.Logger
import com.baarton.runweather.Config
import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.model.UnitSystem
import com.baarton.runweather.model.viewmodel.SettingsViewModel.Companion.DATA_UNIT_TAG
import com.baarton.runweather.model.weather.CurrentWeather
import com.baarton.runweather.repo.WeatherRepository
import com.baarton.runweather.res.SharedRes
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SettingsListener
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class WeatherViewModel(
    settings: ObservableSettings,
    private val config: Config,
    private val weatherRepository: WeatherRepository,
    private val clock: Clock,
    log: Logger
) : ViewModel() {

    companion object {

        fun getImageUrl(imageId: String): String {
            return WeatherRepository.getImageUrl(imageId)
        }
    }

    private val log = log.withTag("WeatherViewModel")

    private var isClosed = false
    private val pollingDispatcher: CoroutineDispatcher = Dispatchers.Default
    private val settingsListener: SettingsListener = settings.addStringListener(DATA_UNIT_TAG, UnitSystem.default().name) {
        onUnitSettingChanged(it)
    }

    private val mutableWeatherState: MutableStateFlow<WeatherViewState> =
        MutableStateFlow(
            WeatherViewState(
                isLoading = true,
                unitSetting = UnitSystem.safeValueOf(settings.getString(DATA_UNIT_TAG, UnitSystem.default().name))
            )
        )

    val weatherState: StateFlow<WeatherViewState> = mutableWeatherState

    init {
        observeWeather()
    }

    private fun onUnitSettingChanged(unitKey: String) {
        mutableWeatherState.update {
            it.copy(unitSetting = UnitSystem.safeValueOf(unitKey))
        }
    }

    override fun onCleared() {
        log.i("Close polling.")
        isClosed = true
        pollingDispatcher.cancel()
        settingsListener.deactivate()
        log.v("Clearing WeatherViewModel")
    }

    private fun observeWeather() {
        val refreshFlow = channelFlow {
            while (!isClosed) {
                log.i("Get WeatherData for poll send.")
                mutableWeatherState.update {
                    it.copy(isLoading = true).also {
                        log.d { "State updated with $it" }
                    }
                }
                try {
                    log.i("Try to refresh WeatherData from flow.")
                    val item = weatherRepository.refreshWeather()
                    send(PollingResult(data = item))
                } catch (exception: Exception) {
                    send(PollingResult(error = exception))
                }
                log.i("Delaying next poll.")
                delay(config.weatherDataRequestInterval)
            }
        }.flowOn(pollingDispatcher)
            .cancellable() //TODO check when polling closes (phone lock, etc) and its config in general

        viewModelScope.launch {
            log.d { "WeatherData refresh coroutine launch." }
            refreshFlow.collect { pollingResult ->
                log.d("Weather collected.")
                updateState(pollingResult)
            }
        }
    }

    private fun updateState(result: PollingResult) {
        mutableWeatherState.update { previousState ->
            if (shouldUpdateState(previousState, result)) {
                previousState.copy(
                    isLoading = false,
                    lastUpdated = result.data?.let { timeStampDuration(it.timestamp) },
                    weather = result.data?.persistedWeather,
                    error = result.error?.let {
                        classifyError(previousState, result.error)
                    }
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

    private fun shouldUpdateState(previousState: WeatherViewState, result: PollingResult): Boolean {
        return previousState.weather != result.data?.persistedWeather ||
            previousState.lastUpdated != result.data?.timestamp?.let { timeStampDuration(it) } ||
            previousState.error != result.error
    }

    private fun timeStampDuration(dataTimestamp: Duration): Duration {
        return clock.now().toEpochMilliseconds().milliseconds - dataTimestamp
    }

    private fun classifyError(previousState: WeatherViewState, error: Throwable): WeatherViewState.ErrorType? {
        return when (error) {
            is WeatherRepository.WeatherDataConsistencyException -> WeatherViewState.ErrorType.DATA_CONSISTENCY
            is WeatherRepository.WeatherAPIException -> WeatherViewState.ErrorType.DATA_PROVIDER
            else -> previousState.error
        }
    }

    fun refreshWeather(): Job {
        log.i("Try to refresh WeatherData one-time.")
        mutableWeatherState.update {
            it.copy(isLoading = true).also {
                log.d { "State updated with $it" }
            }
        }
        return viewModelScope.launch {
            log.i("Launching refresh WeatherData one-time.")
            val oneTimeData = try {
                PollingResult(data = weatherRepository.refreshWeather())
            } catch (exception: Exception) {
                PollingResult(error = exception)
            }

            updateState(oneTimeData)
            log.d { "One-time state update over." }
        }
    }

}

fun PersistedWeather.copy(unitSetting: UnitSystem): PersistedWeather {
    return this.copy(
        weatherList = weatherList,
        locationName = locationName,
        mainData = mainData.copy(
            temperature = unitSetting.tempSwitch(mainData.temperature),
            pressure = unitSetting.pressureSwitch(mainData.pressure),
            humidity = unitSetting.humiditySwitch(mainData.humidity),
        ),
        wind = wind.copy(
            velocity = unitSetting.velocitySwitch(wind.velocity),
            angle = unitSetting.angleSwitch(wind.angle),
        ),
        rain = rain.copy(
            oneHour = unitSetting.heightSwitch(rain.oneHour),
            threeHour = unitSetting.heightSwitch(rain.threeHour)
        ),
        sys = sys
    )
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

data class PollingResult(val data: CurrentWeather? = null, val error: Throwable? = null)

data class WeatherViewState(
    val weather: PersistedWeather? = null,
    val lastUpdated: Duration? = null,
    val error: ErrorType? = null,
    val isLoading: Boolean = false,
    val unitSetting: UnitSystem = UnitSystem.default(),
    val locationAvailable: Boolean = true, //TODO need provider listener logic
    val networkAvailable: Boolean = true //TODO need provider listener logic
) {

    enum class ErrorType(val messageRes: StringResource) {
        DATA_PROVIDER(SharedRes.strings.fragment_weather_results_endpoint_error),
        DATA_CONSISTENCY(SharedRes.strings.fragment_weather_results_data_error),
        INIT_STATE(SharedRes.strings.fragment_weather_results_init_error),
        UNKNOWN(SharedRes.strings.fragment_weather_results_unknown_error),
    }
}