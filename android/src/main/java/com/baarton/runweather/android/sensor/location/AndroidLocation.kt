package com.baarton.runweather.android.sensor.location

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import co.touchlab.kermit.Logger
import com.baarton.runweather.Config
import com.baarton.runweather.sensor.location.Location
import com.baarton.runweather.sensor.location.PlatformLocation
import com.baarton.runweather.util.BooleanListener
import com.baarton.runweather.util.MovementListener
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY


class AndroidLocation(
    private val context: Context,
    private val config: Config,
    private val log: Logger
) : PlatformLocation() {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest: LocationRequest = LocationRequest.Builder(config.locationDataIdealRequestInterval.inWholeMilliseconds).apply {
        // Sets the desired interval for active location updates. This interval is inexact. You
        // may not receive updates at all if no location sources are available, or you may
        // receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        //
        // IMPORTANT NOTE: Apps running on "O" devices (regardless of targetSdkVersion) may
        // receive updates less frequently than this interval when the app is no longer in the
        // foreground.
        // interval = locationDataIdealRequestInterval

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        setMinUpdateIntervalMillis(config.locationDataFastestRequestInterval.inWholeMilliseconds)

        setPriority(PRIORITY_HIGH_ACCURACY)
    }.build()

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            log.i("Received ${locationResult.locations.size} location(s).")

            for (location in locationResult.locations) {
                log.i("New location lat: ${location.latitude} | long: ${location.longitude} | provider: ${location.provider}")
            }

            processLocation(locationResult.lastLocation)
        }

        override fun onLocationAvailability(availability: LocationAvailability) {
            log.i("Location available: ${availability.isLocationAvailable} ")
            processLocationAvailability(availability.isLocationAvailable)
        }
    }

    /**
     * Uses the FusedLocationProvider to start location updates if the correct fine locations are
     * approved.
     *
     * @throws SecurityException if ACCESS_FINE_LOCATION permission is removed before the
     * FusedLocationClient's requestLocationUpdates() has been completed.
     */
    @Throws(SecurityException::class)
    @MainThread
    override fun startLocationUpdates(movementListener: MovementListener, onLocationAvailabilityChange: BooleanListener) {
        super.startLocationUpdates(movementListener, onLocationAvailabilityChange)
        log.i("Start location updates method.")

        val granted = ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
        if (granted != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location: android.location.Location? ->
            log.i("Obtained last location: $location.")

            processLocation(location)
        }

        log.i("Start check on location updates.")

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            log.i("Location updates started.")
        } catch (permissionRevoked: SecurityException) {

            // Exception only occurs if the user revokes the FINE location permission before
            // requestLocationUpdates() is finished executing (very rare).
            log.i("Location permission revoked; details: $permissionRevoked")
            throw permissionRevoked
        }
    }

    private fun processLocation(location: android.location.Location?) {
        // we want to check for nullability, com.google.android.gms.location.LocationResult.getLastLocation actually MIGHT return null
        val lat = location?.latitude
        val lon = location?.longitude

        if (lat != null && lon != null) {
            processLocationInternal(Location(lat, lon))
        } else {
            processLocationInternal(null)
        }
    }

    @MainThread
    override fun stopLocationUpdates() {
        super.stopLocationUpdates()
        log.i("Location updates stopping.")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}