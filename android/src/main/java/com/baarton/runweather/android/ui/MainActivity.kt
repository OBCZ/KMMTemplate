package com.baarton.runweather.android.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import co.touchlab.kermit.Logger
import com.baarton.runweather.android.ui.composables.MainFragmentScreen
import com.baarton.runweather.android.ui.theme.RunWeatherTheme
import com.baarton.runweather.injectLogger
import org.koin.core.component.KoinComponent

//TEST tests with composables?
//TODO handle permissions somehow
class MainActivity : FragmentActivity(), /*PermissionFragment.PermissionListener,*/ KoinComponent {

    //TODO
    // private val locationManager: LocationManager by inject()
    // private val networkManager: NetworkManager by inject()

    private val log: Logger by injectLogger("MainActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO resolve installSplashScreen()
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        //TODO set permission fragment or Composable? which is better?

        // if (!applicationContext.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
        //     requestFineLocationPermission()
        // } else {
             locationPermissionGranted()
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

        setContent {
            RunWeatherTheme {
                MainFragmentScreen()
            }
        }

    }


}
