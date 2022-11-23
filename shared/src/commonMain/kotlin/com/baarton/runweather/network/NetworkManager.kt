package com.baarton.runweather.network

import co.touchlab.kermit.Logger
import kotlin.properties.Delegates


class NetworkManager(
    private val platformNetwork: PlatformNetwork,
    private val log: Logger
) {
    private var isRunning = false
    private var isConnectionAvailableListeners: MutableList<(ConnectionState) -> Unit> = mutableListOf()

    private var _isNetworkConnected: Boolean by Delegates.observable(false) { _, _, newValue ->
        log.i("Connected: $newValue")
        isConnectionAvailableListeners.forEach {
            it(getConnectionState(newValue))
        }
    }

    private fun getConnectionState(available: Boolean): ConnectionState {
        return when (available) {
            true -> ConnectionState.Available
            false -> ConnectionState.Unavailable
        }
    }

    fun startNetworkCallback() {
        if (!isRunning) {
            platformNetwork.startCallback {
                setConnected(it)
            }
            isRunning = true
        }
    }

    fun stopNetworkCallback() {
        if (isRunning) {
            platformNetwork.stopCallback()
            isRunning = false
        }
    }

    private fun setConnected(connected: Boolean) {
        if (connected != _isNetworkConnected) {
            _isNetworkConnected = connected
        }
    }

    fun addConnectionAvailableListener(listener: (ConnectionState) -> Unit) {
        isConnectionAvailableListeners.add(listener)
    }

    fun clearListeners() {
        isConnectionAvailableListeners.clear()
    }

}