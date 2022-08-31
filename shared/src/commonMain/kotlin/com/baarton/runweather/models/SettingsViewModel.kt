package com.baarton.runweather.models

import co.touchlab.kermit.Logger
import com.baarton.runweather.db.CurrentWeather
import com.russhwolf.settings.Settings
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class SettingsViewModel(
    val settings: Settings,
    log: Logger
) : ViewModel() {

    //need some variable that will contain state
    //init state from init, recreation or from preferences?
    //on user change, do appropriate setting change through [setting] call

    private val log = log.withTag("WeatherViewModel")

    private val mutableSettingsState: MutableStateFlow<SettingsViewState> =
        MutableStateFlow(SettingsViewState())

    val settingsState: StateFlow<SettingsViewState> = mutableSettingsState

    init {
        observeSettings()
    }

    override fun onCleared() {
        log.v("Clearing SettingsViewModel")
    }

    private fun observeSettings() {
        //TODO flow declaration
    }
}




//TODO need to initialize from Settings
data class SettingsViewState(
    val unitSetting: DataUnit = DataUnit.METRIC,
    val refreshSetting: Duration = 2.minutes
)

enum class DataUnit {
    METRIC,
    IMPERIAL
}
