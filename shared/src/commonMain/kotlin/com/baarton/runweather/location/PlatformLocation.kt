package com.baarton.runweather.location

import com.baarton.runweather.util.BooleanListener


open class PlatformLocation {

    protected var onLocationAvailabilityChange: BooleanListener? = null

    var currentLocation: Location = Location()
        protected set

    open fun startLocationUpdates(onLocationAvailabilityChange: BooleanListener) {
        this.onLocationAvailabilityChange = onLocationAvailabilityChange
    }

    open fun stopLocationUpdates() {
        onLocationAvailabilityChange = null
    }

}