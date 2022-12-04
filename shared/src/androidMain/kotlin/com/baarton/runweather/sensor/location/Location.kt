package com.baarton.runweather.sensor.location

actual fun Location.distanceTo(other: Location): Float {
    val thisLocation = android.location.Location("")
    thisLocation.latitude = this.latitude
    thisLocation.longitude = this.longitude

    val otherLocation = android.location.Location("")
    otherLocation.latitude = other.latitude
    otherLocation.longitude = other.longitude

    return thisLocation.distanceTo(otherLocation)
}