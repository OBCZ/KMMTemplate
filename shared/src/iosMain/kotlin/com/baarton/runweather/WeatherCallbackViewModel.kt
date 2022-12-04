package com.baarton.runweather

import com.baarton.runweather.repo.WeatherRepository
import com.baarton.runweather.model.viewmodel.WeatherViewModel
import com.baarton.runweather.model.viewmodel.CallbackViewModel
import co.touchlab.kermit.Logger

@Suppress("Unused") // IOS Members are called from Swift - I need every Android view model to be encapsulated like this and called properly
class WeatherCallbackViewModel(
    weatherRepository: WeatherRepository,
    log: Logger
) : CallbackViewModel() {

    override val viewModel = WeatherViewModel(weatherRepository, log) //IOS

    val weather = viewModel.weatherState.asCallbacks()

    fun refreshWeather() {
        viewModel.refreshWeather()
    }

}
