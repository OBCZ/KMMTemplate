package com.baarton.runweather.android.ui.composables

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
import com.baarton.runweather.model.UnitSystem
import com.baarton.runweather.model.viewmodel.SettingsViewModel
import com.baarton.runweather.model.viewmodel.SettingsViewState
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

//TODO attribution section for images (SVGs have their own licenses - not needed to attribute)
// plus the following:
// <a href="https://www.freepik.com/free-photo/beautiful-sky-sunset-sun-clouds-landscape-nature-background_4550584.htm#query=sunset&position=4&from_view=search&track=sph">Image by jcomp</a> on Freepik

//TODO explore https://github.com/uragiristereo/Mejiboard/tree/main/app-alpha/src/main/java/com/github/uragiristereo/mejiboard/presentation/settings
@Composable
fun SettingsFragmentScreen(
) {
    val viewModel = koinViewModel<SettingsViewModel>()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareWeatherFlow = remember(viewModel.settingsState, lifecycleOwner) {
        viewModel.settingsState.flowWithLifecycle(lifecycleOwner.lifecycle)
    }

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
            UnitSystem.METRIC, 2.minutes
        ),
        onUnitSettingChanged = {  },
        onRefreshSettingChanged = {  },
        refreshSteps = 1,
        refreshValueRange = 2f..15f
    )
}