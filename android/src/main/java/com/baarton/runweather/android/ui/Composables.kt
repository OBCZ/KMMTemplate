package com.baarton.runweather.android.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import co.touchlab.kermit.Logger
import com.baarton.runweather.android.R
import com.baarton.runweather.db.CurrentWeather
import com.baarton.runweather.models.WeatherViewModel
import com.baarton.runweather.models.WeatherViewState
import com.baarton.runweather.res.SharedRes
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun MainScreen(
    viewModel: WeatherViewModel,
    log: Logger
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareDogsFlow = remember(viewModel.weatherState, lifecycleOwner) {
        viewModel.weatherState.flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    @SuppressLint("StateFlowValueCalledInComposition") // False positive lint check when used inside collectAsState()
    val weatherState by lifecycleAwareDogsFlow.collectAsState(viewModel.weatherState.value)

    MainScreenContent(
        weatherState = weatherState,
        onRefresh = { viewModel.refreshWeather() },
        onSuccess = { data -> log.v { "View updating with ${data.size} weather" } },
        onError = { exception -> log.e { "Displaying error: $exception" } },
        // onFavorite = { viewModel.updateBreedFavorite(it) }
    )
}

@Composable
fun MainScreenContent(
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
            if (weather != null && weather.size == 1) { //TODO review this check
                LaunchedEffect(weather) {
                    onSuccess(weather)
                }
                WeatherScreen(successData = weather[0])
            } else {
                ErrorScreen("More than one weather")
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
fun EmptyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.empty_breeds))
    }
}

//TODO review
@Composable
fun ErrorScreen(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = error)
    }
}

@Composable
fun WeatherScreen(
    successData: CurrentWeather,
    // favoriteBreed: (Breed) -> Unit
) {
    Column {
        Row(
            Modifier
                // .clickable { onClick(breed) }
                .padding(10.dp)
        ) {

            // val text = StringDesc.Resource(MR.strings.my_string)

            Text(successData.toString(), Modifier.weight(1F))
            Text(text = stringResource(id = SharedRes.strings.app_name.resourceId)) //TODO example for string resources

            //FIXME start with composing UI with svg resources and colors
        }



    }
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
    // MainScreenContent(
        // dogsState = BreedViewState(
        //     breeds = listOf(
        //         Breed(0, "appenzeller", false),
        //         Breed(1, "australian", true)
        //     )
        // )
    // )
}
