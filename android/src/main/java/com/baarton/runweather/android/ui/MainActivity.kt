package com.baarton.runweather.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import co.touchlab.kermit.Logger
import com.baarton.runweather.android.ui.composables.MainScreen
import com.baarton.runweather.android.ui.theme.RunWeatherTheme
import com.baarton.runweather.injectLogger
import org.koin.core.component.KoinComponent

//TEST tests with composables?
class MainActivity : ComponentActivity(), KoinComponent {

    //TODO
    // private val locationManager: LocationManager by inject()
    // private val networkManager: NetworkManager by inject()

    private val log: Logger by injectLogger("MainActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO resolve installSplashScreen()
        // https://developersancho.medium.com/jetpack-compose-splash-screen-api-36ca40c6196b
        // https://proandroiddev.com/animated-splash-screen-in-android-with-compose-4b7dc1baecc5
        super.onCreate(savedInstanceState)

        setContent {
            RunWeatherTheme {
                MainScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // networkManager.startNetworkCallback()
    }

    override fun onPause() {
        super.onPause()
        // locationManager.stopLocationUpdates()
        // networkManager.stopNetworkCallback()
    }
}