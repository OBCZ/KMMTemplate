package com.baarton.runweather.sensor.location

import com.baarton.runweather.util.BooleanListener
import com.baarton.runweather.util.MovementListener


abstract class PlatformLocation {

    private var onLocationAvailabilityChange: BooleanListener? = null
    private var movementListener: MovementListener? = null

    var currentLocation: Location? = null
        private set

    protected fun processLocationAvailability(available: Boolean) {
        if (!available) { currentLocation = null }
        onLocationAvailabilityChange?.invoke(available)
    }

    open fun startLocationUpdates(movementListener: MovementListener, onLocationAvailabilityChange: BooleanListener) {
        this.movementListener = movementListener
        this.onLocationAvailabilityChange = onLocationAvailabilityChange
    }

    open fun stopLocationUpdates() {
        movementListener = null
        onLocationAvailabilityChange = null
    }

    protected fun processLocationInternal(location: Location?) {
        if (location != null) {
            val new = Location(
                location.latitude,
                location.longitude
            )
            val current = Location(
                currentLocation?.latitude ?: 0.0,
                currentLocation?.longitude ?: 0.0
            )
            movementListener?.invoke(Pair(new, current))

            currentLocation = new
            onLocationAvailabilityChange?.invoke(true)
        } else {
            currentLocation = null
            onLocationAvailabilityChange?.invoke(false)
        }
    }

    abstract fun calculateDistance(locationPair: Pair<Location, Location>): Float

}