package com.baarton.runweather.sensor

import kotlin.properties.Delegates


abstract class SensorManager<T : SensorState> {

    private var isRunning = false
    private var isSensorAvailableListeners: MutableList<(T) -> Unit> = mutableListOf()

    private var isSensorAvailable: Boolean by Delegates.observable(false) { _, _, newValue ->
        logAvailabilityChange(newValue)
        isSensorAvailableListeners.forEach { it(getSensorState(newValue)) }
    }

    abstract fun logAvailabilityChange(newAvailability: Boolean)

    abstract fun getSensorState(sensorAvailable: Boolean): T

    protected fun startSensorCallback(listeners: List<(T) -> Unit>?, callbackImpl: () -> Unit) {
        addSensorAvailabilityListeners(listeners)
        if (!isRunning) {
            callbackImpl()
            isRunning = true
        }
    }

    protected fun setAvailable(available: Boolean) {
        if (available != isSensorAvailable) {
            isSensorAvailable = available
        }
    }

    protected fun stopSensorCallback(callbackImpl: () -> Unit) {
        clearListeners()
        if (isRunning) {
            callbackImpl()
            isRunning = false
        }
    }

    private fun addSensorAvailabilityListeners(listeners: List<(T) -> Unit>?) {
        listeners?.forEach {
            isSensorAvailableListeners.add(it)
        }
    }

    private fun clearListeners() {
        isSensorAvailableListeners.clear()
    }

}