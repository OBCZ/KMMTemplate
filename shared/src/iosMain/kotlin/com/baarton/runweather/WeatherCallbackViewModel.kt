package com.baarton.runweather

// import com.baarton.runweather.db.Breed
import com.baarton.runweather.models.WeatherRepository
import com.baarton.runweather.models.WeatherViewModel
import com.baarton.runweather.models.CallbackViewModel
import co.touchlab.kermit.Logger

@Suppress("Unused") // Members are called from Swift
class WeatherCallbackViewModel(
    weatherRepository: WeatherRepository,
    log: Logger
) : CallbackViewModel() {

    override val viewModel = WeatherViewModel(weatherRepository, log)

    val weather = viewModel.weatherState.asCallbacks()

    fun refreshWeather() {
        viewModel.refreshWeather()
    }

    // fun updateBreedFavorite(breed: Breed) {
    //     viewModel.updateBreedFavorite(breed)
    // }
}
