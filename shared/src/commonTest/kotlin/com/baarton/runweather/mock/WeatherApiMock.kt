package com.baarton.runweather.mock

import com.baarton.runweather.ktor.WeatherApi
import com.baarton.runweather.models.weather.Weather
import com.baarton.runweather.models.weather.WeatherData


sealed class MockResponses {
    abstract fun get(): WeatherData
}


object BRNO1 : MockResponses() {
    override fun get(): WeatherData = lazy {
        WeatherData(
            listOf(Weather("800", "Clear", "clear sky", "01d")),
            "Brno1",
            WeatherData.MainData("265.90", "1021", "45"),
            WeatherData.Wind("4.6", "345"),
            null,
            WeatherData.Sys("1646803774", "1646844989")
        )
    }.value
}


object BRNO2 : MockResponses() {
    override fun get(): WeatherData = lazy {
        WeatherData(
            listOf(Weather("800", "Clear", "clear sky", "01d")),
            "Brno2",
            WeatherData.MainData("260.90", "1025", "55"),
            WeatherData.Wind("4.7", "355"),
            null,
            WeatherData.Sys("1646806774", "1646842989")
        )
    }.value
}


object BRNO3 : MockResponses() {
    override fun get(): WeatherData = lazy {
        WeatherData(
            listOf(Weather("800", "Clear", "clear sky", "01d")),
            "Brno3",
            WeatherData.MainData("268.90", "1020", "35"),
            WeatherData.Wind("4.5", "305"),
            null,
            WeatherData.Sys("1646800774", "1646849989")
        )
    }.value
}


class WeatherApiMock : WeatherApi {

    private var resultQueue: ArrayDeque<() -> WeatherData> = initQueue()
    var calledCount = 0
        private set

    override suspend fun getJsonFromApi(): WeatherData {
        val result = resultQueue.removeFirst()()
        calledCount++
        return result
    }

    fun reset() {
        resultQueue = initQueue()
        calledCount = 0
    }

    fun prepareResult(weatherResult: WeatherData) {
        resultQueue = ArrayDeque(listOf({ weatherResult }))
    }

    fun prepareResult(weatherResults: List<WeatherData>) {
        resultQueue = ArrayDeque(weatherResults.map { { it } })
    }

    fun throwOnCall(throwable: Throwable) {
        resultQueue = ArrayDeque(listOf({ throw throwable }))
    }

    private fun initQueue(): ArrayDeque<() -> WeatherData> {
        return ArrayDeque(listOf({ error("Uninitialized!") }))
    }

}