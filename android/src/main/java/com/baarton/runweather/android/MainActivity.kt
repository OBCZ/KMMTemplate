package com.baarton.runweather.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import co.touchlab.kermit.Logger
import com.baarton.runweather.android.ui.MainScreen
import com.baarton.runweather.android.ui.theme.RunWeatherTheme
import com.baarton.runweather.injectLogger
import com.baarton.runweather.models.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

//TODO handle permissions somehow
class MainActivity : ComponentActivity(), /*PermissionFragment.PermissionListener,*/ KoinComponent {

    //TODO
    // private val locationManager: LocationManager by inject()
    // private val networkManager: NetworkManager by inject()

    private val log: Logger by injectLogger("MainActivity")
    private val viewModel: WeatherViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO resolve installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            RunWeatherTheme {
                MainScreen(viewModel, log)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // if (!applicationContext.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
        //     requestFineLocationPermission()
        // } else {
        //     locationPermissionGranted()
        // }
        // networkManager.startNetworkCallback()
    }

    override fun onPause() {
        super.onPause()
        // locationManager.stopLocationUpdates()
        // networkManager.stopNetworkCallback()
    }

    private fun requestFineLocationPermission() {
        // supportFragmentManager
        //     .beginTransaction()
        //     .replace(R.id.main_activity_container_view, PermissionFragment())
        //     .addToBackStack(PermissionFragment.TAG)
        //     .commit()
    }

    // override fun onPermissionGranted() {
    //     locationPermissionGranted()
    // }

    private fun locationPermissionGranted() {
        // locationManager.startLocationUpdates()
        // supportFragmentManager
        //     .beginTransaction()
        //     .replace(R.id.main_activity_container_view, MainFragment())
        //     .addToBackStack(MainFragment.TAG)
        //     .commit()
    }


}
