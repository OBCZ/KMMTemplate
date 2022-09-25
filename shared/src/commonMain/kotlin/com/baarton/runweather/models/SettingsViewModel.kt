package com.baarton.runweather.models

import co.touchlab.kermit.Logger
import com.baarton.runweather.Config
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Duration


//TODO tests
class SettingsViewModel(
    private val settings: Settings,
    private val config: Config,
    log: Logger
) : ViewModel() {

    companion object {
        const val DATA_UNIT_TAG = "DataUnit"
        const val WEATHER_DATA_THRESHOLD_TAG = "RefreshDuration"
    }

    private val log = log.withTag("SettingsViewModel")

    private val mutableSettingsState: MutableStateFlow<SettingsViewState> =
        MutableStateFlow(
            SettingsViewState(
                DataUnit.valueOf(settings.getString(DATA_UNIT_TAG, DataUnit.METRIC.name)),
                Duration.parseIsoString(
                    settings.getString(
                        WEATHER_DATA_THRESHOLD_TAG,
                        config.weatherDataMinimumThreshold.toIsoString()
                    )
                )
            )
        )

    val settingsState: StateFlow<SettingsViewState> = mutableSettingsState

    override fun onCleared() {
        log.v("Clearing SettingsViewModel")
    }

    fun setDataUnit() {
        mutableSettingsState.update { state ->
            state.copy(
                unitSetting = when (mutableSettingsState.value.unitSetting) {
                    DataUnit.METRIC -> DataUnit.IMPERIAL
                    DataUnit.IMPERIAL -> DataUnit.METRIC
                }
            ).also { newState ->
                settings.putString(DATA_UNIT_TAG, newState.unitSetting.name)
            }
        }
    }

    fun setRefreshInterval(newDuration: Duration) {
        mutableSettingsState.update { state ->
            state.copy(refreshSetting = newDuration).also { newState ->
                settings.putString(WEATHER_DATA_THRESHOLD_TAG, newState.refreshSetting.toIsoString())
            }
        }
    }

    fun refreshValueRange(): ClosedFloatingPointRange<Float> {
        return config.weatherDataMinimumThreshold.inWholeMinutes.toFloat()..config.weatherDataMaximumThreshold.inWholeMinutes.toFloat()
    }

    fun refreshSteps(): Int {
        return (config.weatherDataMaximumThreshold - config.weatherDataMinimumThreshold).inWholeMinutes.toInt()
    }
}

data class SettingsViewState(
    val unitSetting: DataUnit,
    val refreshSetting: Duration
)