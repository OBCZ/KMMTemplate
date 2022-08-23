package com.baarton.runweather

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


class AppConfig(private val appInfo: AppInfo) : Config {

    override val preferences: String
        get() = "RUNWEATHER_SETTINGS"

    override val weatherDataRequestInterval: Duration
        get() = (if (appInfo.debug) { 10L } else { 60L }).seconds

    override val weatherDataRefreshDistance: Float
        get() = 500f

    override val locationDataIdealRequestInterval: Duration
        get() = (if (appInfo.debug) { 10L } else { 120L }).seconds

    override val locationDataFastestRequestInterval: Duration
        get() = (if (appInfo.debug) { 5L } else { 60L }).seconds

}