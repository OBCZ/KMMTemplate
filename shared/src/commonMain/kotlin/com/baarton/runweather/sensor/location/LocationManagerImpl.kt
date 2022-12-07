package com.baarton.runweather.sensor.location

import co.touchlab.kermit.Logger
import com.baarton.runweather.sensor.SensorManager
import com.baarton.runweather.sensor.SensorState.LocationState
import com.baarton.runweather.util.LocationStateListener
import com.baarton.runweather.util.MovementListener

class LocationManagerImpl(
    private val platformLocation: PlatformLocation,
    private val log: Logger
) : SensorManager<LocationState>(), LocationManager {

    override fun logAvailabilityChange(newAvailability: Boolean) {
        log.i("Location available: ${newAvailability}.")
    }

    override fun getSensorState(sensorAvailable: Boolean): LocationState {
        return when (sensorAvailable) {
            true -> LocationState.Available
            false -> LocationState.Unavailable
        }
    }

    override fun start(listeners: List<LocationStateListener>?, movementListener: MovementListener?) {
        startSensorCallback(listeners) {
            platformLocation.startLocationUpdates(movementListener) {
                setAvailable(it)
            }
        }
    }

    override fun stop() {
        stopSensorCallback {
            platformLocation.stopLocationUpdates()
        }
    }

    override fun currentLocation(): Location? {
        return platformLocation.currentLocation
    }

    override fun calculateDistance(locationPair: Pair<Location, Location>): Float {
        return platformLocation.calculateDistance(locationPair)
    }

}