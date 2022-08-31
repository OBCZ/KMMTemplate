package com.baarton.runweather.android.ui.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import co.touchlab.kermit.Logger
import com.baarton.runweather.models.SettingsViewModel
import com.baarton.runweather.models.WeatherViewModel
import org.koin.androidx.compose.inject
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf


//TODO explore https://github.com/uragiristereo/Mejiboard/tree/main/app-alpha/src/main/java/com/github/uragiristereo/mejiboard/presentation/settings
@Composable
fun SettingsFragmentScreen(
) {
    //TODO we can inject like that into composables
    val viewModel: SettingsViewModel by viewModel()
    val log: Logger by inject { parametersOf("SettingsFragment") }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareWeatherFlow = remember(viewModel.settingsState, lifecycleOwner) {
        viewModel.settingsState.flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    @SuppressLint("StateFlowValueCalledInComposition") // False positive lint check when used inside collectAsState()
    val weatherState by lifecycleAwareWeatherFlow.collectAsState(viewModel.settingsState.value)

    Column(
        content = {
            UnitSetting()
            RefreshSetting()
        }, modifier = Modifier.fillMaxSize()

    )

}

@Composable
fun RefreshSetting() {
    //TODO setting composables
    // SliderPref(
    //     key = "sp1",
    //     title = "Slider example with custom range and value shown on side",
    //     valueRange = 50f..200f,
    //     showValue = true
    // )
    Text(text = "Refresh Settings")
}

@Composable
fun UnitSetting() {
    //TODO setting composables
    // ListPref(
    //     key = "l1",
    //     title = "ListPref example",
    //     summary = "Opens up a dialog of options",
    //     entries = mapOf(
    //         "0" to "Entry 1",
    //         "1" to "Entry 2",
    //         "2" to "Entry 3",
    //         "3" to "Entry 4",
    //         "4" to "Entry 5"
    //     )
    // )
    Text(text = "Unit Settings")
}
