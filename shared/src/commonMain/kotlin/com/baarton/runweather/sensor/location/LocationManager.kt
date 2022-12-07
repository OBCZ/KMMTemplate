package com.baarton.runweather.sensor.location

import com.baarton.runweather.sensor.SensorState
import com.baarton.runweather.util.LocationStateListener
import com.baarton.runweather.util.MovementListener

interface LocationManager {

    /**
     * Starts platform specific location updates callbacks. Methods provides possibilities for passing listeners for Location [SensorState] change and for new [Location] data updates, that should be coming in regularly.
     *
     * @param listeners A collection of separate [LocationStateListener]s that are all triggered on every new [SensorState.LocationState] update. Optional.
     * @param movementListener A listener that is triggered on every new [Location] data update. Optional.
     */
    fun start(listeners: List<LocationStateListener>? = null, movementListener: MovementListener? = null)

    /**
     * Stops platform specific location updates' callbacks. Clears all listeners, provided that they were specified.
     */
    fun stop()

    /**
     * Returns current location of the device.
     *
     * @return [Location] object. Can be null - in this case, either the sensor returned invalid data, or the data was not initialized yet.
     */
    fun currentLocation(): Location?

    /**
     * Calculates platform specific implementation of distance between two points on an Earth globe.
     *
     * @param locationPair Input [Pair] of two [Location] points.
     * @return Number value in meters.
     */
    fun calculateDistance(locationPair: Pair<Location, Location>): Float

}