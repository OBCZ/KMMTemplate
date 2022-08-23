package com.baarton.runweather

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


object TestConfig : Config {

    override val preferences: String = "RUNWEATHER_SETTINGS_TEST"
    override val weatherDataRequestInterval: Duration = 1.seconds
    override val weatherDataRefreshDistance: Float = 50f
    override val locationDataIdealRequestInterval: Duration = 2.seconds
    override val locationDataFastestRequestInterval: Duration = 1.seconds

}