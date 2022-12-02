package com.baarton.runweather.sensor

sealed class SensorState {

    sealed class ConnectionState : SensorState() {
        object Available : ConnectionState()
        object Unavailable : ConnectionState()
    }

    sealed class LocationState : SensorState() {
        object Available : LocationState()
        object Unavailable : LocationState()
    }

}