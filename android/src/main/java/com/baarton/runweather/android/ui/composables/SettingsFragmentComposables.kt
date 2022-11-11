package com.baarton.runweather.android.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import com.baarton.runweather.android.ui.AndroidVector.build
import com.baarton.runweather.model.UnitSystem
import com.baarton.runweather.model.viewmodel.SettingsViewModel
import com.baarton.runweather.model.viewmodel.SettingsViewState
import com.baarton.runweather.res.SharedRes
import com.baarton.runweather.ui.Vector
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

//TODO attribution section for images (SVGs have their own licenses - not needed to attribute)
// plus the following:
// <a href="https://www.freepik.com/free-photo/beautiful-sky-sunset-sun-clouds-landscape-nature-background_4550584.htm#query=sunset&position=4&from_view=search&track=sph">Image by jcomp</a> on Freepik


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
            UnitSetting(settingsState) {
                onUnitSettingChanged()
            }
            RefreshSetting(settingsState.refreshSetting.inWholeMinutes, refreshValueRange, refreshSteps) {
                onRefreshSettingChanged(it)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun UnitSetting(state: SettingsViewState, onUnitSettingClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable {
                onUnitSettingClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .fillMaxWidth(0.75f),
            imageVector = Vector.THERMOSTAT.build(),
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .weight(4f)
                .wrapContentHeight()
        ) {
            Text(
                text = stringResource(id = SharedRes.strings.settings_units_title.resourceId),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Start
            )
            Text(
                text = stringResource(id = state.unitSetting.textRes.resourceId),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun RefreshSetting(
    wholeMinutes: Long,
    refreshValueRange: ClosedFloatingPointRange<Float>,
    refreshSteps: Int,
    onRefreshSettingChanged: (Duration) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .fillMaxWidth(0.75f),
            imageVector = Vector.REFRESH.build(),
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .weight(4f)
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = SharedRes.strings.settings_poll_freq_title.resourceId),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Start
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    modifier = Modifier
                        .wrapContentSize()
                        .weight(8f),
                    value = wholeMinutes.toFloat(),
                    valueRange = refreshValueRange,
                    steps = refreshSteps,
                    onValueChange = { onRefreshSettingChanged(it.toDouble().minutes) }
                )
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .weight(2f),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onBackground,
                    text = wholeMinutes.toString(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenContentPreview() {
    SettingsFragmentScreenContent(
        settingsState = SettingsViewState(
            UnitSystem.METRIC, 2.minutes
        ),
        onUnitSettingChanged = { },
        onRefreshSettingChanged = { },
        refreshSteps = 1,
        refreshValueRange = 2f..15f
    )
}