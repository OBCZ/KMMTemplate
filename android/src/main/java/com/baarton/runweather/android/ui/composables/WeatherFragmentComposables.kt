package com.baarton.runweather.android.ui.composables

import android.annotation.SuppressLint
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import co.touchlab.kermit.Logger
import com.baarton.runweather.android.R
import com.baarton.runweather.db.CurrentWeather
import com.baarton.runweather.models.Weather
import com.baarton.runweather.models.WeatherData
import com.baarton.runweather.models.WeatherViewModel
import com.baarton.runweather.models.WeatherViewState
import com.baarton.runweather.models.lastUpdatedResId
import com.baarton.runweather.res.SharedRes
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.inject
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf


@Composable
fun WeatherFragmentScreen(
) {
    //TODO we can inject like that into composables
    val viewModel: WeatherViewModel by viewModel()
    val log: Logger by inject { parametersOf("WeatherFragment") }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareDogsFlow = remember(viewModel.weatherState, lifecycleOwner) {
        viewModel.weatherState.flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    @SuppressLint("StateFlowValueCalledInComposition") // False positive lint check when used inside collectAsState()
    val weatherState by lifecycleAwareDogsFlow.collectAsState(viewModel.weatherState.value)

    WeatherFragmentScreenContent(
        weatherState = weatherState,
        onRefresh = { viewModel.refreshWeather() },
        onSuccess = { data -> log.v { "View updating with ${data.size} weather" } },
        onError = { exception -> log.e { "Displaying error: $exception" } },
        // onFavorite = { viewModel.updateBreedFavorite(it) }
    )
}

@Composable
private fun WeatherFragmentScreenContent(
    weatherState: WeatherViewState,
    onRefresh: () -> Unit = {},
    onSuccess: (List<CurrentWeather>) -> Unit = {},
    onError: (String) -> Unit = {},
    // onFavorite: (Breed) -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        SwipeRefresh( //TODO do I want this swipe refresh?
            state = rememberSwipeRefreshState(isRefreshing = weatherState.isLoading),
            onRefresh = onRefresh
        ) {
            if (weatherState.isEmpty) {
                EmptyScreen()
            }
            val weather = weatherState.weather
            if (weather == null) { //TODO review this check
                ErrorScreen("Weather null")
            } else if (weather.size == 1) {
                LaunchedEffect(weather) {
                    onSuccess(weather)
                }
                WeatherScreen(weather[0], weatherState)
            } else {
                WeatherScreen(weather[weather.size - 1], weatherState)
                // ErrorScreen("More than one weather")
            }
            val error = weatherState.error
            if (error != null) {
                LaunchedEffect(error) {
                    onError(error)
                }
                ErrorScreen(error)
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
        Text(stringResource(R.string.empty_breeds))
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
    }
}

//TODO how much can I extract with iOS to common from the UI building blocks (expect/actual abstraction)?
@Composable
private fun WeatherScreen(
    successData: CurrentWeather,
    state: WeatherViewState
) {
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
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_location_on_24_primary),
                contentDescription = "TODO"
            )
            Image(
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(8.dp)
                    .weight(1f),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_network_on_24_primary),
                contentDescription = "TODO"
            )
            Text(
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(8.dp)
                    .weight(8f),
                text = lastUpdatedText(state.lastUpdated)
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
                Text(successData.locationName)
                Row {
                    //TODO img from service
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_about_24_primary),
                        contentDescription = "TODO"
                    )
                    Text(text = successData.weatherList[0].description)
                    // Img, Text
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(4f)
            ) { // 2
                Text(text = successData.mainData.temperature)
                // Text
            }
        }

    }
}

//TODO can we extract more?
@Composable
private fun lastUpdatedText(lastUpdated: Long): String {
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
        ))
}

// @Composable
// fun DogRow(breed: CurrentWeather/*, onClick: (Breed) -> Unit*/) {
//     Row(
//         Modifier
//             // .clickable { onClick(breed) }
//             .padding(10.dp)
//     ) {
//         Text(breed.name, Modifier.weight(1F))
//         FavoriteIcon(breed)
//     }
// }

// @Composable
// fun FavoriteIcon(breed: Breed) {
//     Crossfade(
//         targetState = !breed.favorite,
//         animationSpec = TweenSpec(
//             durationMillis = 500,
//             easing = FastOutSlowInEasing
//         )
//     ) { fav ->
//         if (fav) {
//             Image(
//                 painter = painterResource(id = R.drawable.ic_favorite_border_24px),
//                 contentDescription = stringResource(R.string.favorite_breed, breed.name)
//             )
//         } else {
//             Image(
//                 painter = painterResource(id = R.drawable.ic_favorite_24px),
//                 contentDescription = stringResource(R.string.unfavorite_breed, breed.name)
//             )
//         }
//     }
// }

@Preview
@Composable
fun MainScreenContentPreview_Success() {
    WeatherFragmentScreenContent(
        weatherState = WeatherViewState(
            weather = listOf(
                CurrentWeather(
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
                    rain = null,
                    sys = WeatherData.Sys(
                        sunrise = "1657681500",
                        sunset = "1657739161"
                    )
                )

            )
        )
    )
}
