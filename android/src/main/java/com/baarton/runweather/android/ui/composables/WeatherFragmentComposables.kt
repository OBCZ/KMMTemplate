package com.baarton.runweather.android.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import com.baarton.runweather.android.ui.AndroidVector.build
import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.models.WeatherViewModel
import com.baarton.runweather.models.WeatherViewState
import com.baarton.runweather.models.lastUpdatedResId
import com.baarton.runweather.models.weather.Weather
import com.baarton.runweather.models.weather.WeatherData
import com.baarton.runweather.res.SharedRes
import com.baarton.runweather.ui.Vector
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.viewModel
import kotlin.time.Duration

@Composable
fun WeatherFragmentScreen(
) {
    //TODO we can inject like that into composables
    val viewModel: WeatherViewModel by viewModel()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareWeatherFlow = remember(viewModel.weatherState, lifecycleOwner) {
        viewModel.weatherState.flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    val weatherState by lifecycleAwareWeatherFlow.collectAsState(viewModel.weatherState.value)

    WeatherFragmentScreenContent(
        weatherState = weatherState,
        onRefresh = { viewModel.refreshWeather() },
    )
}

@Composable
private fun WeatherFragmentScreenContent(
    weatherState: WeatherViewState,
    onRefresh: () -> Unit = {},
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh( //TODO do I want this swipe refresh?
            state = rememberSwipeRefreshState(isRefreshing = weatherState.isLoading),
            onRefresh = onRefresh
        ) {
            val weather = weatherState.weather
            if (weather == null) { //TODO review this check
                ErrorScreen("Weather null")
            } else {

                WeatherScreen(weather, weatherState.lastUpdated)
            }
            val error = weatherState.error
            if (error != null) {

                ErrorScreen(stringResource(id = error.messageRes.resourceId))
            }
        }
    }
}

//TODO review
@Composable
private fun EmptyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally,
    ) {
        Text("Empty")
    }
}

//TODO review
@Composable
private fun ErrorScreen(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally,
    ) {
        Text(text = error)
        //TODO dont forget explicit button refresh
    }
}

//TODO how much can I extract with iOS to common from the UI building blocks (expect/actual abstraction)?
@Composable
private fun WeatherScreen(weatherData: PersistedWeather, lastUpdated: Duration?) {
    //TODO img background
    Column {
        //TODO network, location row 1
        Row(
            Modifier
                .align(CenterHorizontally)
                .padding(4.dp)
                .weight(1f)
                .fillMaxWidth()
        ) {
            // TODO Img 1, Img 1, Text 8
            //
            Image(
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(8.dp)
                    .weight(1f),
                imageVector = Vector.LOCATION_OFF.build(),
                contentDescription = "TODO"
            )
            Image(
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(8.dp)
                    .weight(1f),
                imageVector = Vector.LOCATION_ON.build(),
                contentDescription = "TODO"
            )
            Text(
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(8.dp)
                    .weight(8f),
                text = lastUpdatedText(lastUpdated)
            )
            //TODO example for string resources

        }

        //TODO data row 5
        Row(
            modifier = Modifier
                .weight(5f)
                .fillMaxWidth()

        ) {
            Column() {
                //TODO data rows
                // always info Img, num slow Text, num fast Text
                Row() {
                    // header row

                }
                Row() {
                    // torso row
                }
                Row() {
                    // legs row
                }
                //...

            }
            Column() {
                //TODO 7 rows
                // Img, Text
                Row() {
                    // humidity row
                }
                Row() {
                    // rainfall row
                }
                //...
            }

        }

        //TODO warning row 2
        Row(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
        ) {
            //TODO temp warning
            Column() {
                Row() {
                    // Img, Img
                }
                Row() {
                    // Text
                }
            }
            //TODO wind warning
            Column() {
                Row() {
                    // Img, Img
                }
                Row() {
                    // Text
                }
            }
        }

        //TODO main data row 2
        Row(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(6f)
            ) { // 3
                Text(weatherData.locationName)
                Row {
                    //TODO img from service
                    Image(
                        imageVector = Vector.ABOUT.build(),
                        contentDescription = "TODO"
                    )
                    Text(text = weatherData.weatherList[0].description)
                    // Img, Text
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(4f)
            ) { // 2
                Text(text = weatherData.mainData.temperature)
                // Text
            }
        }

    }
}

//TODO can we extract more?
@Composable
private fun lastUpdatedText(lastUpdated: Duration?): String {
    val pair = lastUpdatedResId(lastUpdated)
    val lastUpdatedValue = pair.second?.let {
        stringResource(id = pair.first.resourceId, formatArgs = arrayOf(it))
    } ?: run {
        stringResource(id = pair.first.resourceId)
    }

    return stringResource(
        id = SharedRes.strings.fragment_weather_last_updated_text.resourceId,
        formatArgs = arrayOf(
            lastUpdatedValue
        )
    )
}

@Preview
@Composable
fun MainScreenContentPreview_Success() {
    WeatherFragmentScreenContent(
        weatherState = WeatherViewState(
            weather =
            PersistedWeather(
                weatherList = listOf(
                    Weather(
                        weatherId = "803",
                        title = "Clouds",
                        description = "oblačno",
                        iconId = "04d"
                    )
                ),
                locationName = "Kouřim",
                mainData = WeatherData.MainData(
                    temperature = "300.82",
                    pressure = "1019",
                    humidity = "38"
                ),
                wind = WeatherData.Wind(speed = "4.27", deg = "277"),
                rain = WeatherData.Rain(oneHour = "0.58", threeHour = "1.36"),
                sys = WeatherData.Sys(
                    sunrise = "1657681500",
                    sunset = "1657739161"
                )
            )
        )
    )
}
