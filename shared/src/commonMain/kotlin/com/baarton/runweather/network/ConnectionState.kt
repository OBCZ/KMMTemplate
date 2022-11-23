package com.baarton.runweather.network


sealed class ConnectionState {
    object Available : ConnectionState()
    object Unavailable : ConnectionState()
}