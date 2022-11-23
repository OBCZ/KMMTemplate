package com.baarton.runweather.network

import com.baarton.runweather.util.BooleanListener

open class PlatformNetwork {

    protected var onConnectionChange: BooleanListener? = null

    open fun startCallback(onConnectionChange: (Boolean) -> Unit) {
        this.onConnectionChange = onConnectionChange
    }

    open fun stopCallback() {
        this.onConnectionChange = null
    }

}