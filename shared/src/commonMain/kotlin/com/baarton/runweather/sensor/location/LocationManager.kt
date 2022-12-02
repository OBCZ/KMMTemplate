package com.baarton.runweather.sensor.location

import co.touchlab.kermit.Logger
import com.baarton.runweather.sensor.SensorManager
import com.baarton.runweather.sensor.SensorState.LocationState


class LocationManager(
    private val platformLocation: PlatformLocation,
    private val log: Logger
) : SensorManager<LocationState>() {

    // private var isLocationAvailable: Boolean by Delegates.observable(false) { _, _, newValue ->
    //     log.i("Location available: ${newValue}.")

        // isLocationAvailableListeners.forEach { it(getLocationState(newValue)) }
    // }

    // private var isLocationAvailableListeners: MutableList<(LocationState) -> Unit> = mutableListOf()

    override fun logAvailabilityChange(newAvailability: Boolean) {
        log.i("Location available: ${newAvailability}.")
    }

    override fun getSensorState(sensorAvailable: Boolean): LocationState {
        return when (sensorAvailable) {
            true -> LocationState.Available
            false -> LocationState.Unavailable
        }
    }

    fun start(listeners: List<(LocationState) -> Unit>) {
        startSensorCallback(listeners) {
            platformLocation.startLocationUpdates {
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

    // fun startLocationCallback() {
    //     if (!isRunning) {
    //         platformLocation.startLocationUpdates {
    //             if (it != isLocationAvailable) {
    //                 setAvailable(it)
    //             }
    //         }
    //         isRunning = true
    //     }
    // fun addLocationAvailableListener(listener: (LocationState) -> Unit) {
    //     isLocationAvailableListeners.add(listener)


    // }

    fun stop() {
        stopSensorCallback {
            platformLocation.stopLocationUpdates()

        }
        // if (isRunning) {
        //     platformLocation.stopLocationUpdates()
        //     isRunning = false
        // }
    }

    // }

    // fun clearListeners() {
    //     isLocationAvailableListeners.clear()
    // }

    fun currentLocation(): Location {
        return platformLocation.currentLocation
    }

    // fun start(listeners: List<(SensorState) -> Unit>) {
    //     TODO("Not yet implemented")
    // }
}

data class Location(val latitude: Double = 0.0, val longitude: Double = 0.0)