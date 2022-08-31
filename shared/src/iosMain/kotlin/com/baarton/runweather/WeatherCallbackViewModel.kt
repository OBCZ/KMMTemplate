package com.baarton.runweather

import com.baarton.runweather.models.WeatherRepository
import com.baarton.runweather.models.WeatherViewModel
import com.baarton.runweather.models.CallbackViewModel
import co.touchlab.kermit.Logger

@Suppress("Unused") // FIXME Members are called from Swift - I need every Android view model to be encapsulated like this and called properly
class WeatherCallbackViewModel(
    weatherRepository: WeatherRepository,
    log: Logger
) : CallbackViewModel() {

    override val viewModel = WeatherViewModel(weatherRepository, log) //FIXME

    val weather = viewModel.weatherState.asCallbacks()

    fun refreshWeather() {
        viewModel.refreshWeather()
    }

}
