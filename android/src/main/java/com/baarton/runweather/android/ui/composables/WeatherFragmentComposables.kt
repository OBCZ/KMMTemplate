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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.flowWithLifecycle
import com.baarton.runweather.android.ui.AndroidVector.build
import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.model.DataUnit
import com.baarton.runweather.model.MeasureUnit
import com.baarton.runweather.model.RainfallUnit
import com.baarton.runweather.model.WindDirection
import com.baarton.runweather.model.viewmodel.WeatherViewModel
import com.baarton.runweather.model.viewmodel.WeatherViewState
import com.baarton.runweather.model.viewmodel.lastUpdatedResId
import com.baarton.runweather.model.weather.Weather
import com.baarton.runweather.model.weather.WeatherData
import com.baarton.runweather.res.SharedRes
import com.baarton.runweather.ui.Vector
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes


@Composable
fun WeatherFragmentScreen(
) {
    val viewModel = koinViewModel<WeatherViewModel>()

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
            with(weatherState) {
                val error = this.error
                val weather = this.weather
                when {
                    error == null && weather == null -> ErrorScreen("Weather empty")
                    error == null && weather != null -> WeatherScreen(this.copy(weather = weather))
                    error != null && weather == null -> ErrorScreen(stringResource(id = error.messageRes.resourceId))
                    else -> ErrorScreen("Unknown error")
                }
            }
        }
    }
}

//TODO review
// dont forget explicit button refresh
// make it better overall
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
private fun WeatherScreen(weatherState: WeatherViewState) {
    val weather = weatherState.weather!! // we should not get NPE here
    val locationAvailable = weatherState.locationAvailable
    val networkAvailable = weatherState.networkAvailable
    val lastUpdated = weatherState.lastUpdated
    val unitSetting = weatherState.unitSetting

    //TODO img background
    Column {
        StateRow(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(4.dp)
                .weight(1f)
                .fillMaxWidth(),
            locationAvailable = locationAvailable,
            networkAvailable = networkAvailable,
            lastUpdated = lastUpdated,
        )

        DataRow(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(4.dp)
                .weight(5f)
                .fillMaxWidth(),
            weather = weather,
            unitSetting = unitSetting
        )

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
                Text(weather.locationName)
                Row {
                    //TODO img from service
                    Image(
                        imageVector = Vector.ABOUT.build(),
                        contentDescription = "TODO"
                    )
                    Text(text = weather.weatherList[0].description)
                    // Img, Text
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(4f)
            ) { // 2
                Text(text = dataText(weather.mainData.temperature, unitSetting.tempUnit))
                // Text
            }
        }

    }
}

@Composable
private fun StateRow(modifier: Modifier, locationAvailable: Boolean, networkAvailable: Boolean, lastUpdated: Duration?) {
    Row(modifier = modifier) {
        Image(
            modifier = Modifier
                .align(CenterVertically)
                .padding(8.dp)
                .weight(1f),
            imageVector = if (locationAvailable) {
                Vector.LOCATION_ON
            } else {
                Vector.LOCATION_OFF
            }.build(),
            contentDescription = stringResource(
                id = SharedRes.strings.fragment_weather_location_content_description.resourceId,
                formatArgs = arrayOf(onOffText(locationAvailable))
            )
        )
        Image(
            modifier = Modifier
                .align(CenterVertically)
                .padding(8.dp)
                .weight(1f),
            imageVector = if (networkAvailable) {
                Vector.NETWORK_ON
            } else {
                Vector.NETWORK_OFF
            }.build(),
            contentDescription = stringResource(
                id = SharedRes.strings.fragment_weather_network_content_description.resourceId,
                formatArgs = arrayOf(onOffText(networkAvailable))
            )
        )
        Text(
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(CenterVertically)
                .padding(8.dp)
                .weight(8f),
            text = lastUpdatedText(lastUpdated)
        )
    }
}

@Composable
private fun onOffText(available: Boolean): String {
    return stringResource(
        if (available) {
            SharedRes.strings.app_on
        } else {
            SharedRes.strings.app_off
        }.resourceId
    )
}

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

@Composable
private fun DataRow(modifier: Modifier, weather: PersistedWeather, unitSetting: MeasureUnit) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(2.dp)
                .weight(1f)
                .fillMaxWidth()
        ) {
            val columnModifier = Modifier
                .align(CenterVertically)
                .weight(1f)
                .fillMaxWidth()

            with(weather) {
                WeatherDataColumn(columnModifier, Vector.RAIN, dataText(mainData.humidity, unitSetting.humidityUnit))
                WeatherDataColumn(columnModifier, Vector.DROPS, rainfallText(unitSetting.rainfallUnit, rain?.oneHour, rain?.threeHour))
                WeatherDataColumn(columnModifier, Vector.PRESSURE, dataText(mainData.pressure, unitSetting.pressureUnit))
                WeatherDataColumn(columnModifier, Vector.DIRECTION, windDirectionText(wind.deg))
                WeatherDataColumn(columnModifier, Vector.WIND, dataText(wind.speed, unitSetting.windSpeedUnit))
                WeatherDataColumn(columnModifier, Vector.SUNRISE, timeText(sys.sunrise))
                WeatherDataColumn(columnModifier, Vector.SUNSET, timeText(sys.sunset))
            }
        }

        Row(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(4.dp)
                .weight(3f)
                .fillMaxWidth()
        ) {

        }

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
}

@Composable
private fun WeatherDataColumn(modifier: Modifier, vector: Vector, dataText: String) {
    Column(
        modifier = modifier
    ) {
        Image(
            modifier = Modifier
                .align(CenterHorizontally)
                .weight(1f)
                .fillMaxWidth(0.5f),
            imageVector = vector.build(),
            contentDescription = vector.name
        )
        Text(
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(CenterHorizontally)
                .fillMaxWidth()
                .weight(1f),
            text = dataText,
            fontSize = 10.sp //TODO extract to typography

        )
    }
}

@Composable
private fun rainfallText(rainfallUnit: RainfallUnit, oneHour: String?, threeHour: String?): String {
    val oneHourText = dataText(oneHour, rainfallUnit)
    val threeHourText = dataText(threeHour, rainfallUnit)
    return "${oneHourText}\n${threeHourText}"
}

@Composable
private fun windDirectionText(deg: String): String {
    return deg.toFloatOrNull()?.let {
        stringResource(id = WindDirection.signRes(it).resourceId)
    } ?: stringResource(id = SharedRes.strings.app_n_a.resourceId)
}

@Composable
private fun dataText(dataValue: String?, dataUnit: DataUnit): String {
    return (dataValue ?: "0").toFloatOrNull()?.let {
        stringResource(id = dataUnit.stringRes.resourceId, formatArgs = arrayOf(dataUnit.prepareValue(it)))
    } ?: stringResource(id = SharedRes.strings.app_n_a.resourceId)
}

private fun timeText(timeStampSeconds: String): String {
    return Instant.fromEpochSeconds(timeStampSeconds.toLong())
        .toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("HH:MM"))
}

@Preview
@Composable
fun MainScreenContentPreview_Success() {
    WeatherFragmentScreenContent(
        weatherState = WeatherViewState(
            weather = PersistedWeather(
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
                rain = WeatherData.Rain(oneHour = "0.58", threeHour = null),
                sys = WeatherData.Sys(
                    sunrise = "1657681500",
                    sunset = "1657739161"
                )
            ),
            lastUpdated = 5.minutes
        )
    )
}
