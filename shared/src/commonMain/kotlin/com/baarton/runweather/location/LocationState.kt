package com.baarton.runweather.location

sealed class LocationState {
    object Available : LocationState()
    object Unavailable : LocationState()
}