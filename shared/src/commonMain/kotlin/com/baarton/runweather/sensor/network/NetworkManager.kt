package com.baarton.runweather.sensor.network

import co.touchlab.kermit.Logger
import com.baarton.runweather.sensor.SensorManager
import com.baarton.runweather.sensor.SensorState.ConnectionState

class NetworkManager(
    private val platformNetwork: PlatformNetwork,
    private val log: Logger
) : SensorManager<ConnectionState>() {

    override fun logAvailabilityChange(newAvailability: Boolean) {
        log.i("Connection available: ${newAvailability}.")
    }

    override fun getSensorState(sensorAvailable: Boolean): ConnectionState {
        return when (sensorAvailable) {
            true -> ConnectionState.Available
            false -> ConnectionState.Unavailable
        }
    }

    fun start(listeners: List<(ConnectionState) -> Unit>) {
        startSensorCallback(listeners) {
            platformNetwork.startCallback {
                setConnected(it)
            }
        }
    }

    fun stop() {
        stopSensorCallback {
            platformNetwork.stopCallback()
        }
    }

    private fun setConnected(connected: Boolean) {
        if (connected != isSensorAvailable) {
            isSensorAvailable = connected
        }
    }

}