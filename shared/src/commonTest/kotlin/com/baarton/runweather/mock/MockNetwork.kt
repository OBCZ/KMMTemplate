package com.baarton.runweather.mock

import com.baarton.runweather.sensor.network.PlatformNetwork
import com.baarton.runweather.util.BooleanListener
import kotlin.properties.Delegates

class MockNetwork : PlatformNetwork() {

    private var callbackRegistered = false

    private var isConnected: Boolean by Delegates.observable(false) { _, _, newValue ->
        if (callbackRegistered) {
            onConnectionChange?.invoke(newValue)
        }
    }

    override fun startCallback(onConnectionChange: BooleanListener) {
        super.startCallback(onConnectionChange)
        callbackRegistered = true
    }

    override fun stopCallback() {
        super.stopCallback()
        callbackRegistered = false
    }

    fun mockConnected(connected: Boolean) {
        isConnected = connected
    }

}