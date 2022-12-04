package com.baarton.runweather.sensor.location

data class Location(val latitude: Double, val longitude: Double)

expect fun Location.distanceTo(other: Location): Float