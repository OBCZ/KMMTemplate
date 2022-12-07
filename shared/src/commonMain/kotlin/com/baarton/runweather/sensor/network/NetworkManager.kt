package com.baarton.runweather.sensor.network

import com.baarton.runweather.sensor.SensorState
import com.baarton.runweather.util.NetworkStateListener

interface NetworkManager {

    /**
     * Starts platform specific network updates callbacks. Methods provides possibilities for passing listeners for Connection [SensorState] change.
     *
     * @param listeners A collection of separate [NetworkStateListener]s that are all triggered on every new [SensorState.ConnectionState] update. Optional.
     */
    fun start(listeners: List<NetworkStateListener>? = null)

    /**
     * Stops platform specific network updates' callbacks. Clears all listeners, provided that they were specified.
     */
    fun stop()

}