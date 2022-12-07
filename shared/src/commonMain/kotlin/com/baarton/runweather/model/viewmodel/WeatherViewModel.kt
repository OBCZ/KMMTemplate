package com.baarton.runweather.model.viewmodel

import co.touchlab.kermit.Logger
import com.baarton.runweather.Config
import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.model.UnitSystem
import com.baarton.runweather.model.viewmodel.SettingsViewModel.Companion.DATA_UNIT_TAG
import com.baarton.runweather.model.weather.CurrentWeather
import com.baarton.runweather.repo.WeatherRepository
import com.baarton.runweather.res.SharedRes
import com.baarton.runweather.sensor.SensorState.*
import com.baarton.runweather.sensor.location.LocationManager
import com.baarton.runweather.sensor.network.NetworkManager
import com.baarton.runweather.util.LocationStateListener
import com.baarton.runweather.util.MovementListener
import com.baarton.runweather.util.NetworkStateListener
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
    private val locationManager: LocationManager,
    private val networkManager: NetworkManager,
    private val clock: Clock,
    private val log: Logger
) : ViewModel() {

    companion object {

        fun getImageUrl(imageId: String): String {
            return WeatherRepository.getImageUrl(imageId)
        }

        fun lastUpdatedResId(timestampAge: Duration?): Pair<StringResource, Long?> {
            return when (timestampAge?.inWholeSeconds) {
                null -> SharedRes.strings.app_n_a to null
                0L -> SharedRes.strings.weather_last_updated_now to null
                in 1L..59L -> SharedRes.strings.weather_last_updated_sec_time to timestampAge.inWholeSeconds
                else -> SharedRes.strings.weather_last_updated_min_time to timestampAge.inWholeMinutes
            }
        }
    }

    private var isClosed = false
    private val pollingDispatcher: CoroutineDispatcher = Dispatchers.Default
    private val settingsListener: SettingsListener = settings.addStringListener(DATA_UNIT_TAG, UnitSystem.default().name) {
        onUnitSettingChanged(it)
    }

    private val movementListener: MovementListener = {
        with(locationManager.calculateDistance(it)) {
            log.i("Location distance between last two: $this meters.")
            if (this >= config.weatherDataRefreshDistance) {
                settings.putLong(WeatherRepository.DB_TIMESTAMP_KEY, 0)
            }
        }
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
        networkManager.start(networkListeners())
        locationManager.start(locationListeners(), movementListener)
        observeWeather()
    }

    private fun onUnitSettingChanged(unitKey: String) {
        mutableWeatherState.update {
            it.copy(unitSetting = UnitSystem.safeValueOf(unitKey))
        }.also {
            log.d { "Updating weather state with $it." }
        }
    }

    override fun onCleared() {
        log.i("Close polling.")
        isClosed = true
        pollingDispatcher.cancel()
        settingsListener.deactivate()
        networkManager.stop()
        locationManager.stop()
        log.v("Clearing WeatherViewModel.")
    }

    private fun locationListeners(): List<LocationStateListener> {
        return listOf(
            { locationState ->
                mutableWeatherState.update {
                    it.copy(
                        locationState = locationState
                    ).also {
                        log.d { "Updating weather state with $it." }
                    }
                }
            },
            {
                if (it == LocationState.Available) {
                    refreshWeather()
                }
            }
        )
    }

    private fun networkListeners(): List<NetworkStateListener> {
        return listOf(
            { connectionState ->
                mutableWeatherState.update {
                    it.copy(
                        networkState = connectionState
                    ).also {
                        log.d { "Updating weather state with $it." }
                    }
                }
            },
            {
                if (it == ConnectionState.Available) {
                    refreshWeather()
                }
            }
        )
    }

    private fun observeWeather() {
        val refreshFlow = channelFlow {
            while (!isClosed) {
                log.i("Get WeatherData for poll send.")
                mutableWeatherState.update {
                    it.copy(isLoading = true).also {
                        log.d { "State updated with $it." }
                    }
                }
                try {
                    log.i("Try to refresh WeatherData from flow.")
                    val item = weatherRepository.refreshWeather(locationManager.currentLocation())
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
            is WeatherRepository.LocationConsistencyException -> WeatherViewState.ErrorType.LOCATION_CONSISTENCY
            is WeatherRepository.WeatherAPIException -> WeatherViewState.ErrorType.DATA_PROVIDER
            else -> previousState.error
        }
    }

    fun refreshWeather(): Job {
        isClosed = true
        log.i("Try to refresh WeatherData one-time.")
        mutableWeatherState.update {
            it.copy(isLoading = true).also {
                log.d { "State updated with $it." }
            }
        }
        return viewModelScope.launch {
            log.i("Launching refresh WeatherData one-time.")
            val oneTimeData = try {
                PollingResult(data = weatherRepository.refreshWeather(locationManager.currentLocation()))
            } catch (exception: Exception) {
                PollingResult(error = exception)
            }

            updateState(oneTimeData)
            log.d { "One-time state update over." }
        }.also {
            isClosed = false
        }
    }

}

fun PersistedWeather.convert(unitSetting: UnitSystem): PersistedWeather {
    return this.copy(
        weatherList = weatherList,
        locationName = locationName,
        mainData = mainData.copy(
            temperature = unitSetting.tempConversion(mainData.temperature),
            pressure = unitSetting.pressureConversion(mainData.pressure),
            humidity = unitSetting.humidityConversion(mainData.humidity),
        ),
        wind = wind.copy(
            velocity = unitSetting.velocityConversion(wind.velocity),
            angle = unitSetting.angleConversion(wind.angle),
        ),
        rain = rain.copy(
            oneHour = unitSetting.heightConversion(rain.oneHour),
            threeHour = unitSetting.heightConversion(rain.threeHour)
        ),
        sys = sys
    )
}

data class PollingResult(val data: CurrentWeather? = null, val error: Throwable? = null)

data class WeatherViewState(
    val weather: PersistedWeather? = null,
    val lastUpdated: Duration? = null,
    val error: ErrorType? = null,
    val isLoading: Boolean = false,
    val unitSetting: UnitSystem = UnitSystem.default(),
    val locationState: LocationState = LocationState.Unavailable,
    val networkState: ConnectionState = ConnectionState.Unavailable
) {

    enum class ErrorType(val messageRes: StringResource) {
        DATA_PROVIDER(SharedRes.strings.weather_results_endpoint_error),
        DATA_CONSISTENCY(SharedRes.strings.weather_results_data_error),
        LOCATION_CONSISTENCY(SharedRes.strings.weather_results_location_error),
        INIT_STATE(SharedRes.strings.weather_results_init_error),
        UNKNOWN(SharedRes.strings.weather_results_unknown_error),
    }
}