package com.baarton.runweather.model.viewmodel

import app.cash.turbine.test
import com.baarton.runweather.Config
import com.baarton.runweather.StateFlowTest
import com.baarton.runweather.TestAppInfo
import com.baarton.runweather.TestConfig
import com.baarton.runweather.model.UnitSystem
import com.baarton.runweather.testLogger
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds


class SettingsViewModelTest : StateFlowTest() {

    private var logger = testLogger()
    private val settingsMock = MapSettings()
    private val testConfig: Config = TestConfig

    private val viewModel by lazy { SettingsViewModel(settingsMock, testConfig, TestAppInfo, logger) }

    companion object {

        private val defaultInitSettingsState = SettingsViewState(
            unitSetting = UnitSystem.METRIC,
            refreshSetting = 2.seconds
        )
    }

    @Test
    fun `Settings default init test`() = runBlocking {
        viewModel.settingsState.test {
            assertEquals(
                defaultInitSettingsState,
                awaitItem()
            )
        }
    }

    @Test
    fun `Settings modified config init test`() = runBlocking {
        settingsMock.putString(SettingsViewModel.DATA_UNIT_TAG, UnitSystem.IMPERIAL.name)
        settingsMock.putString(SettingsViewModel.WEATHER_DATA_THRESHOLD_TAG, 3.seconds.toIsoString())

        viewModel.settingsState.test {
            assertEquals(
                SettingsViewState(
                    unitSetting = UnitSystem.IMPERIAL,
                    refreshSetting = 3.seconds
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `Settings set Data Unit test`() = runBlocking {
        viewModel.settingsState.test {
            viewModel.setDataUnit()

            assertEquals(
                defaultInitSettingsState.copy(unitSetting = UnitSystem.IMPERIAL),
                awaitItemAfter(defaultInitSettingsState)
            )

            viewModel.setDataUnit()

            assertEquals(
                defaultInitSettingsState,
                awaitItemAfter(defaultInitSettingsState.copy(unitSetting = UnitSystem.IMPERIAL))
            )
        }
    }

    @Test
    fun `Settings set Refresh interval test`() = runBlocking {
        viewModel.settingsState.test {
            viewModel.setRefreshInterval(10.seconds)

            assertEquals(
                defaultInitSettingsState.copy(refreshSetting = 10.seconds),
                awaitItemAfter(defaultInitSettingsState)
            )

            viewModel.setRefreshInterval(5.seconds)

            assertEquals(
                defaultInitSettingsState.copy(refreshSetting = 5.seconds),
                awaitItemAfter(defaultInitSettingsState.copy(refreshSetting = 10.seconds))
            )
        }
    }

}