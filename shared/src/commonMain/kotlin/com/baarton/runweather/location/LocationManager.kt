package com.baarton.runweather.location

import co.touchlab.kermit.Logger
import kotlin.properties.Delegates

//FIXME extract both sensor manager?
//FIXME extract states (also methods in HomePageComposables)?

class LocationManager(
    private val platformLocation: PlatformLocation,
    private val log: Logger
) {

    private var isRunning = false
    private var isLocationAvailable: Boolean by Delegates.observable(false) { _, _, newValue ->
        log.i("Location available: ${newValue}.")

        isLocationAvailableListeners.forEach { it(getLocationState(newValue)) }
    }

    private var isLocationAvailableListeners: MutableList<(LocationState) -> Unit> = mutableListOf()

    private fun getLocationState(available: Boolean): LocationState {
        return when (available) {
            true -> LocationState.Available
            false -> LocationState.Unavailable
        }
    }

    fun startLocationCallback() {
        if (!isRunning) {
            platformLocation.startLocationUpdates {
                if (it != isLocationAvailable) {
                    setAvailable(it)
                }
            }
            isRunning = true
        }
    }

    fun stopLocationCallback() {
        if (isRunning) {
            platformLocation.stopLocationUpdates()
            isRunning = false
        }
    }

    fun addLocationAvailableListener(listener: (LocationState) -> Unit) {
        isLocationAvailableListeners.add(listener)
    }

    private fun setAvailable(available: Boolean) {
        if (available != isLocationAvailable) {
            isLocationAvailable = available
        }
    }

    fun clearListeners() {
        isLocationAvailableListeners.clear()
    }

    fun currentLocation(): Location {
        return platformLocation.currentLocation
    }

}

data class Location(val latitude: Double = 0.0, val longitude: Double = 0.0)