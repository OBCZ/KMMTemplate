package com.baarton.runweather.util

import com.baarton.runweather.sensor.location.Location
import kotlin.math.pow
import kotlin.math.roundToInt


typealias BooleanListener = (Boolean) -> Unit
typealias MovementListener = (Pair<Location, Location>) -> Unit

fun Float.roundDecimals(numDecimals: Int): Float {
    val factor = 10.0.pow(numDecimals.toDouble())
    return (this * factor).roundToInt() / factor.toFloat()
}