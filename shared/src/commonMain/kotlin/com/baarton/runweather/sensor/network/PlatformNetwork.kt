package com.baarton.runweather.sensor.network

import com.baarton.runweather.util.BooleanListener

open class PlatformNetwork {

    private var onConnectionChange: BooleanListener? = null

    protected fun processNetworkAvailability(available: Boolean) {
        onConnectionChange?.invoke(available)
    }

    open fun startCallback(onConnectionChange: BooleanListener) {
        this.onConnectionChange = onConnectionChange
    }

    open fun stopCallback() {
        this.onConnectionChange = null
    }

}