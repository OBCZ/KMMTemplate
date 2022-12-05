package com.baarton.runweather.mock

import com.baarton.runweather.randomLocation
import com.baarton.runweather.sensor.location.Location
import com.baarton.runweather.sensor.location.PlatformLocation
import com.baarton.runweather.util.BooleanListener
import com.baarton.runweather.util.MovementListener
import kotlin.properties.Delegates

class MockLocation : PlatformLocation() {

    private var callbackRegistered = false

    private var isAvailable: Boolean by Delegates.observable(false) { _, _, newValue ->
        if (callbackRegistered) {
            processLocationAvailability(newValue)
        }
    }

    override fun startLocationUpdates(movementListener: MovementListener, onLocationAvailabilityChange: BooleanListener) {
        super.startLocationUpdates(movementListener, onLocationAvailabilityChange)
        callbackRegistered = true
    }

    override fun stopLocationUpdates() {
        super.stopLocationUpdates()
        callbackRegistered = false
    }

    fun mockAvailable(available: Boolean) {
        isAvailable = available
    }

    fun mockLocation(location: Location? = randomLocation()) {
        processLocationInternal(location)
    }

}
