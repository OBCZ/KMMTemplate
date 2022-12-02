package com.baarton.runweather.sensor.location

import com.baarton.runweather.util.BooleanListener


open class PlatformLocation {

    protected var onLocationAvailabilityChange: BooleanListener? = null

    var currentLocation: Location = Location()
        protected set

    open fun startLocationUpdates(onLocationAvailabilityChange: BooleanListener) {
        this.onLocationAvailabilityChange = onLocationAvailabilityChange
    }

    open fun stopLocationUpdates() {
        onLocationAvailabilityChange = null
    }

    //FIXME I need this (on Repo level or here?) + tests (only cache timestamp reset is needed - settings.putLong(DB_TIMESTAMP_KEY, timeStamp))
    // private fun clearCacheIfUserMoved(last: Location, current: Location) {
    //     with(last.distanceTo(current)) {
    //         logger.info("Location distance between last two: $this meters.")
    //         if (this >= weatherDataRefreshDistance) {
    //             CoroutineScope(EmptyCoroutineContext).launch {
    //                 WeatherRepository.clear()
    //             }
    //         }
    //     }
    // }

}