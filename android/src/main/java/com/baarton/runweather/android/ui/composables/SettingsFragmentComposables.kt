package com.baarton.runweather.android.ui.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import co.touchlab.kermit.Logger
import com.baarton.runweather.models.DataUnit
import com.baarton.runweather.models.SettingsViewModel
import com.baarton.runweather.models.SettingsViewState
import org.koin.androidx.compose.inject
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes


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
    val settingsState by lifecycleAwareWeatherFlow.collectAsState(viewModel.settingsState.value)

    SettingsFragmentScreenContent(
        settingsState = settingsState,
        onUnitSettingChanged = { viewModel.setDataUnit() },
        refreshValueRange = viewModel.refreshValueRange(),
        refreshSteps = viewModel.refreshSteps(),
        onRefreshSettingChanged = { viewModel.setRefreshInterval(it) }
    )
}

@Composable
fun SettingsFragmentScreenContent(
    settingsState: SettingsViewState,
    onUnitSettingChanged: () -> Unit,
    refreshValueRange: ClosedFloatingPointRange<Float>,
    refreshSteps: Int,
    onRefreshSettingChanged: (Duration) -> Unit
) {
    Column(
        content = {
            UnitSetting(settingsState.unitSetting.name) {
                onUnitSettingChanged()
            }
            RefreshSetting(settingsState.refreshSetting.inWholeMinutes, refreshValueRange, refreshSteps) {
                onRefreshSettingChanged(it)
            }
        }, modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun RefreshSetting(
    wholeMinutes: Long,
    refreshValueRange: ClosedFloatingPointRange<Float>,
    refreshSteps: Int,
    onRefreshSettingChanged: (Duration) -> Unit
) {
    Text(text = "Refresh Settings")

    Text(text = wholeMinutes.toString())
    Slider(
        value = wholeMinutes.toFloat(),
        valueRange = refreshValueRange,
        steps = refreshSteps,
        onValueChange = { onRefreshSettingChanged(it.toDouble().minutes) }
    )
}

//TODO so far we are nto using the changes anywhere
@Composable
fun UnitSetting(text: String, onUnitSettingClick: () -> Unit) {
    Text(text = "Unit Settings")

    Button(onClick = onUnitSettingClick, modifier = Modifier.padding(16.dp)) {
        Text(text = text)
    }
}

@Preview
@Composable
fun SettingsScreenContentPreview() {
    SettingsFragmentScreenContent(
        settingsState = SettingsViewState(
            DataUnit.METRIC, 2.minutes
        ),
        onUnitSettingChanged = {  },
        onRefreshSettingChanged = {  },
        refreshSteps = 1,
        refreshValueRange = 2f..15f
    )
}