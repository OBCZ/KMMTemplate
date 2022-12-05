package com.baarton.runweather.mock

import com.baarton.runweather.randomLocation
import com.baarton.runweather.sensor.location.Location
import com.baarton.runweather.sensor.location.PlatformLocation
import com.baarton.runweather.util.BooleanListener
import com.baarton.runweather.util.MovementListener
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
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

    override fun calculateDistance(locationPair: Pair<Location, Location>): Float {
        val lon1 = locationPair.first.longitude.toRadian()
        val lon2 = locationPair.second.longitude.toRadian()
        val lat1 = locationPair.first.latitude.toRadian()
        val lat2 = locationPair.second.latitude.toRadian()

        // Haversine formula
        val dlon = lon2 - lon1
        val dlat = lat2 - lat1
        val a = (
            sin(dlat / 2).pow(2)
                + (cos(lat1) * cos(lat2) * sin(dlon / 2).pow(2))
            )

        val c = 2 * asin(sqrt(a))

        // Radius of earth in meters
        val r = 6378.0 * 1000

        return (c * r).toFloat()
    }

}

fun Double.toRadian(): Double = this / 180 * PI