package com.baarton.runweather

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


class AppConfig(private val appInfo: AppInfo) : Config {

    override val preferences: String
        get() = "RUNWEATHER_SETTINGS"

    override val weatherDataRequestInterval: Duration
        get() = (if (appInfo.debug) { 10 } else { 60 }).seconds

    override val weatherDataMinimumThreshold: Duration
        get() = (if (appInfo.debug) { 1 } else { 2 }).minutes

    override val weatherDataMaximumThreshold: Duration
        get() = 15.minutes

    override val weatherDataRefreshDistance: Float
        get() = 500f

    override val locationDataIdealRequestInterval: Duration
        get() = (if (appInfo.debug) { 10 } else { 120 }).seconds

    override val locationDataFastestRequestInterval: Duration
        get() = (if (appInfo.debug) { 5 } else { 60 }).seconds

}