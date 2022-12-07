package com.baarton.runweather.sensor.network

import co.touchlab.kermit.Logger
import com.baarton.runweather.sensor.SensorManager
import com.baarton.runweather.sensor.SensorState.ConnectionState
import com.baarton.runweather.util.NetworkStateListener

class NetworkManagerImpl(
    private val platformNetwork: PlatformNetwork,
    private val log: Logger
) : SensorManager<ConnectionState>(), NetworkManager {

    override fun logAvailabilityChange(newAvailability: Boolean) {
        log.i("Connection available: ${newAvailability}.")
    }

    override fun getSensorState(sensorAvailable: Boolean): ConnectionState {
        return when (sensorAvailable) {
            true -> ConnectionState.Available
            false -> ConnectionState.Unavailable
        }
    }

    override fun start(listeners: List<NetworkStateListener>?) {
        startSensorCallback(listeners) {
            platformNetwork.startCallback {
                setAvailable(it)
            }
        }
    }

    override fun stop() {
        stopSensorCallback {
            platformNetwork.stopCallback()
        }
    }

}