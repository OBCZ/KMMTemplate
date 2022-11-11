package com.baarton.runweather.model.viewmodel

import co.touchlab.kermit.Logger
import com.baarton.runweather.AppInfo
import com.baarton.runweather.Config
import com.baarton.runweather.model.UnitSystem
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Duration


class SettingsViewModel(
    private val settings: ObservableSettings,
    private val config: Config,
    private val appInfo: AppInfo,
    private val log: Logger
) : ViewModel() {

    companion object {
        const val DATA_UNIT_TAG = "DataUnit"
        const val WEATHER_DATA_THRESHOLD_TAG = "RefreshDuration"
    }

    private val mutableSettingsState: MutableStateFlow<SettingsViewState> =
        MutableStateFlow(
            SettingsViewState(
                UnitSystem.safeValueOf(settings.getString(DATA_UNIT_TAG, UnitSystem.default().name)),
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
        log.v("Clearing SettingsViewModel.")
    }

    fun setDataUnit() {
        mutableSettingsState.update { state ->
            state.copy(
                unitSetting = when (mutableSettingsState.value.unitSetting) {
                    UnitSystem.METRIC -> UnitSystem.IMPERIAL
                    UnitSystem.IMPERIAL -> UnitSystem.METRIC
                }
            ).also { newState ->
                settings.putString(DATA_UNIT_TAG, newState.unitSetting.name)
                log.d { "Updating setting state with $newState." }
            }
        }
    }

    fun setRefreshInterval(newDuration: Duration) {
        mutableSettingsState.update { state ->
            state.copy(
                refreshSetting = newDuration
            ).also { newState ->
                settings.putString(WEATHER_DATA_THRESHOLD_TAG, newState.refreshSetting.toIsoString())
                log.d { "Updating setting state with $newState." }
            }
        }
    }

    fun refreshValueRange(): ClosedFloatingPointRange<Float> {
        return config.weatherDataMinimumThreshold.inWholeMinutes.toFloat()..config.weatherDataMaximumThreshold.inWholeMinutes.toFloat()
    }

    fun refreshSteps(): Int {
        return (config.weatherDataMaximumThreshold - config.weatherDataMinimumThreshold).inWholeMinutes.toInt()
    }

    fun appInfo(): AppInfo {
        return appInfo
    }
}

data class SettingsViewState(
    val unitSetting: UnitSystem,
    val refreshSetting: Duration
)