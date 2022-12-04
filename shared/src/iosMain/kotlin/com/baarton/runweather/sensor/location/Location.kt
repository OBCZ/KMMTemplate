package com.baarton.runweather.sensor.location

actual fun Location.distanceTo(other: Location): Float {
    return 0f //IOS
}