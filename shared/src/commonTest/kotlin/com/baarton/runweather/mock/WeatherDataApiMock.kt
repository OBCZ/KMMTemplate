package com.baarton.runweather.mock

import com.baarton.runweather.ktor.WeatherDataApi
import com.baarton.runweather.model.weather.WeatherData


class WeatherDataApiMock : WeatherDataApi {

    private var resultQueue: ArrayDeque<() -> WeatherData> = initQueue()
    var calledCount = 0
        private set

    override suspend fun getWeatherFromApi(): WeatherData {
        val result = resultQueue.removeFirst()()
        calledCount++
        return result
    }

    fun reset() {
        resultQueue = initQueue()
        calledCount = 0
    }

    fun prepareResults(vararg results: Any) {
        resultQueue.clear()
        results.forEach {
            when (it) {
                is WeatherData -> resultQueue.add { it }
                is Throwable -> resultQueue.add { throw it }
                else -> error("Not supported type for API mock result queue.")
            }
        }
    }

    private fun initQueue(): ArrayDeque<() -> WeatherData> {
        return ArrayDeque(listOf({ error("Uninitialized!") }))
    }

}