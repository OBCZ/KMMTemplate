package com.baarton.runweather.mock

import com.baarton.runweather.sensor.location.PlatformLocation
import com.baarton.runweather.util.BooleanListener
import kotlin.properties.Delegates

class MockLocation : PlatformLocation() {

    private var callbackRegistered = false

    private var isAvailable: Boolean by Delegates.observable(false) { _, _, newValue ->
        if (callbackRegistered) {
            onLocationAvailabilityChange?.invoke(newValue)
        }
    }

    override fun startLocationUpdates(onLocationAvailabilityChange: BooleanListener) {
        super.startLocationUpdates(onLocationAvailabilityChange)
        callbackRegistered = true
    }

    override fun stopLocationUpdates() {
        super.stopLocationUpdates()
        callbackRegistered = false
    }

    fun mockAvailable(available: Boolean) {
        isAvailable = available
    }

}
