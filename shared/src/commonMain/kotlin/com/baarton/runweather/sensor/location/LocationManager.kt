package com.baarton.runweather.sensor.location

import co.touchlab.kermit.Logger
import com.baarton.runweather.sensor.SensorManager
import com.baarton.runweather.sensor.SensorState.LocationState
import com.baarton.runweather.util.MovementListener

class LocationManager(
    private val platformLocation: PlatformLocation,
    private val log: Logger
) : SensorManager<LocationState>() {

    override fun logAvailabilityChange(newAvailability: Boolean) {
        log.i("Location available: ${newAvailability}.")
    }

    override fun getSensorState(sensorAvailable: Boolean): LocationState {
        return when (sensorAvailable) {
            true -> LocationState.Available
            false -> LocationState.Unavailable
        }
    }

    fun start(listeners: List<(LocationState) -> Unit>, movementListener: MovementListener) {
        startSensorCallback(listeners) {
            platformLocation.startLocationUpdates(movementListener) {
                if (it != isSensorAvailable) {
                    setAvailable(it)
                }
            }
        }
    }

    private fun setAvailable(available: Boolean) {
        if (available != isSensorAvailable) {
            isSensorAvailable = available
        }
    }

    fun stop() {
        stopSensorCallback {
            platformLocation.stopLocationUpdates()

        }
    }

    fun currentLocation(): Location? {
        return platformLocation.currentLocation
    }

}