package com.baarton.runweather.util

import kotlin.math.pow
import kotlin.math.roundToInt


typealias BooleanListener = (Boolean) -> Unit

fun Float.roundDecimals(numDecimals: Int): Float {
    val factor = 10.0.pow(numDecimals.toDouble())
    return (this * factor).roundToInt() / factor.toFloat()
}