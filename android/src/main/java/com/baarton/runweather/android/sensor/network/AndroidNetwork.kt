package com.baarton.runweather.android.sensor.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import co.touchlab.kermit.Logger
import com.baarton.runweather.sensor.network.PlatformNetwork
import com.baarton.runweather.util.BooleanListener


class AndroidNetwork(context: Context, private val log: Logger) : PlatformNetwork() {

    private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            log.i("Network $network available.")
            processNetworkAvailability(true)
        }

        override fun onLost(network: Network) {
            log.i("Network $network lost.")
            processNetworkAvailability(false)
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            log.i("Network $network capabilities changed.")
            processNetworkAvailability(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
        }
    }

    override fun startCallback(onConnectionChange: BooleanListener) {
        super.startCallback(onConnectionChange)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun stopCallback() {
        super.stopCallback()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

}