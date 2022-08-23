package com.baarton.runweather

import kotlin.time.Duration

interface Config {

    /*
     * Name of the SharedPrefs file. Should stay constant.
     */
    val preferences: String

    /*
     * Value of the time interval for requesting the WeatherData for polling. The data might come from various sources after individual polls.
     */
    val weatherDataRequestInterval: Duration

    /*
     * Value of the distance threshold for invalidating the cached WeatherData. Assuming the users could be moving when using the app. Meters.
     */
    val weatherDataRefreshDistance: Float //UPGRADE Settings candidate

    /*
     * Value of the time interval for requesting the Location data.
     */
    val locationDataIdealRequestInterval: Duration

    /*
     * Value of the time interval for requesting the Location data (fastest).
     */
    val locationDataFastestRequestInterval: Duration

}