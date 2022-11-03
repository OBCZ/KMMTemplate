package com.baarton.runweather.android.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import com.baarton.runweather.android.ui.AndroidVector.build
import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.model.Angle
import com.baarton.runweather.model.Angle.Companion.deg
import com.baarton.runweather.model.Height
import com.baarton.runweather.model.Height.Companion.mm
import com.baarton.runweather.model.Humidity.Companion.percent
import com.baarton.runweather.model.Pressure.Companion.hpa
import com.baarton.runweather.model.QuantityUnit
import com.baarton.runweather.model.Temperature.Companion.celsius
import com.baarton.runweather.model.Velocity.Companion.mps
import com.baarton.runweather.model.WindDirection
import com.baarton.runweather.model.viewmodel.RunnersHint
import com.baarton.runweather.model.viewmodel.RunnersInfo
import com.baarton.runweather.model.viewmodel.TemperatureHint
import com.baarton.runweather.model.viewmodel.WarningHint
import com.baarton.runweather.model.viewmodel.WeatherHint
import com.baarton.runweather.model.viewmodel.WeatherViewModel
import com.baarton.runweather.model.viewmodel.WeatherViewState
import com.baarton.runweather.model.viewmodel.copy
import com.baarton.runweather.model.viewmodel.lastUpdatedResId
import com.baarton.runweather.model.weather.Weather
import com.baarton.runweather.model.weather.WeatherData
import com.baarton.runweather.model.weather.WeatherId
import com.baarton.runweather.res.SharedRes
import com.baarton.runweather.ui.Vector
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.icerock.moko.resources.StringResource
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
                    error == null && weather != null -> WeatherScreen(copy(weather = weather))
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
    val weather = weatherState.weather!!.copy(weatherState.unitSetting) // we should not get NPE here
    val locationAvailable = weatherState.locationAvailable
    val networkAvailable = weatherState.networkAvailable
    val lastUpdated = weatherState.lastUpdated

    //UPGRADE change images according to the weather conditions
    Column(
        Modifier.paint(
            painter = painterResource(SharedRes.images.beautiful_sunset.drawableResId),
            contentScale = ContentScale.Crop
        )
    ) {
        StateRow(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(2.dp)
                .weight(1f)
                .fillMaxWidth(),
            locationAvailable = locationAvailable,
            networkAvailable = networkAvailable,
            lastUpdated = lastUpdated
        )

        DataRow(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(6.dp)
                .weight(6f)
                .fillMaxWidth(),
            weather = weather
        )

        WarningRow(
            modifier = Modifier
                .weight(2f)
                .align(CenterHorizontally)
                .fillMaxSize(),
            weather = weather,
        )

        MainRow(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(6.dp)
                .weight(3f)
                .fillMaxWidth(),
            weather = weather
        )
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
            modifier = Modifier
                .align(CenterVertically)
                .padding(8.dp)
                .weight(8f),
            text = lastUpdatedText(lastUpdated),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground
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
private fun DataRow(modifier: Modifier, weather: PersistedWeather) {
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
                WeatherDataColumn(columnModifier, Vector.HUMIDITY, dataText(mainData.humidity))
                WeatherDataColumn(columnModifier, Vector.RAIN, rainfallText(rain.oneHour, rain.threeHour))
                WeatherDataColumn(columnModifier, Vector.PRESSURE, dataText(mainData.pressure))
                WeatherDataColumn(columnModifier, Vector.DIRECTION, windDirectionText(wind.angle))
                WeatherDataColumn(columnModifier, Vector.WIND, dataText(wind.velocity))
                WeatherDataColumn(columnModifier, Vector.SUNRISE, timeText(sys.sunrise))
                WeatherDataColumn(columnModifier, Vector.SUNSET, timeText(sys.sunset))
            }
        }

        Column(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(4.dp)
                .weight(4f)
                .fillMaxSize()

        ) {

            val rowModifier = Modifier
                .align(CenterHorizontally)
                .weight(1f)
                .fillMaxSize()

            //TODO header row?

            InfoRow(
                rowModifier,
                weather,
                SharedRes.strings.weather_runners_info_data_head_cover_category,
                RunnersInfo.HeadCover
            )
            InfoRow(
                rowModifier,
                weather,
                SharedRes.strings.weather_runners_info_data_sunglasses_category,
                RunnersInfo.Sunglasses
            )
            InfoRow(
                rowModifier,
                weather,
                SharedRes.strings.weather_runners_info_data_neck_cover_category,
                RunnersInfo.NeckCover
            )
            InfoRow(
                rowModifier,
                weather,
                SharedRes.strings.weather_runners_info_data_top_layers_category,
                RunnersInfo.LayersTop
            )
            InfoRow(
                rowModifier,
                weather,
                SharedRes.strings.weather_runners_info_data_gloves_category,
                RunnersInfo.Gloves
            )
            InfoRow(
                rowModifier,
                weather,
                SharedRes.strings.weather_runners_info_data_bottom_layers_category,
                RunnersInfo.LayersBottom
            )
            InfoRow(
                rowModifier,
                weather,
                SharedRes.strings.weather_runners_info_data_socks_category,
                RunnersInfo.Socks
            )

        }

    }
}

@Composable
private fun WeatherDataColumn(modifier: Modifier, vector: Vector, dataText: String) {
    Column(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(0.66f),
            imageVector = vector.build(),
            contentDescription = vector.name
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Center
        ) {
            Text(
                text = dataText,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Composable
private fun rainfallText(oneHour: Height, threeHour: Height): String {
    val oneHourText = dataText(oneHour)
    val threeHourText = dataText(threeHour)
    return "${oneHourText}\n${threeHourText}"
}

@Composable
private fun windDirectionText(angle: Angle): String {
    return stringResource(id = WindDirection.signRes(angle).resourceId)
}

@Composable
private fun dataText(quantityUnit: QuantityUnit): String {
    return stringResource(id = quantityUnit.unit.stringRes.resourceId, formatArgs = arrayOf(quantityUnit.formattedValue()))
}

private fun timeText(instant: Instant): String {
    return instant
        .toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("HH:MM"))
}

@Composable
private fun InfoRow(modifier: Modifier, weatherData: PersistedWeather, categoryTextRes: StringResource, hint: RunnersHint) {
    val slowColumnHint = when (hint) {
        is TemperatureHint -> hint.slow(weatherData.mainData.temperature)
        is WeatherHint -> hint.hint(weatherData)
    }

    val fastColumnHint = when (hint) {
        is TemperatureHint -> hint.fast(weatherData.mainData.temperature)
        is WeatherHint -> hint.hint(weatherData)
    }

    Row(
        modifier
            .wrapContentHeight()
            .background(color = MaterialTheme.colors.primary, shape = MaterialTheme.shapes.medium)
            .padding(6.dp),
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 1.dp, horizontal = 2.dp)
                .weight(0.33f),
            text = stringResource(id = categoryTextRes.resourceId),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.body2
        )
        Text(
            modifier = Modifier
                .padding(vertical = 1.dp, horizontal = 2.dp)
                .weight(0.33f),
            text = stringResource(id = slowColumnHint.textRes.resourceId),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.body2
        )
        Text(
            modifier = Modifier
                .padding(vertical = 1.dp, horizontal = 2.dp)
                .weight(0.33f),
            text = stringResource(id = fastColumnHint.textRes.resourceId),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
private fun WarningRow(modifier: Modifier, weather: PersistedWeather) {
    val temperatureWarning = RunnersInfo.TemperatureWarning.warning(weather)
    val windWarning = RunnersInfo.WindWarning.warning(weather)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = CenterVertically
    ) {
        val warningItemModifier = Modifier
            .weight(1f)

        temperatureWarning?.let { WarningItem(warningItemModifier, it, Vector.THERMOSTAT) }
        windWarning?.let { WarningItem(warningItemModifier, it, Vector.WIND) }
    }
}

@Composable
private fun WarningItem(modifier: Modifier, warningHint: WarningHint, warningVector: Vector) {
    Box(
        modifier = modifier,
        contentAlignment = Center
    ) {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colors.secondary, shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 24.dp, vertical = 8.dp)

        ) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 8.dp, end = 4.dp),
                    imageVector = warningHint.vector.build(), contentDescription = null
                )
                Image(
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 4.dp, end = 8.dp),
                    imageVector = warningVector.build(), contentDescription = null
                )
            }
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
                    text = stringResource(id = warningHint.textRes.resourceId),
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

@Composable
private fun MainRow(modifier: Modifier, weather: PersistedWeather) {
    Row(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(3f)
                .align(CenterVertically),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = weather.locationName,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h2
            )
            Row(
                modifier = Modifier
                    .padding(2.dp)
            ) {
                //TODO img from service
                Image(
                    imageVector = Vector.ABOUT.build(),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 2.dp),
                    text = weather.weatherList[0].description,
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.body1
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(2f)
                .align(CenterVertically),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dataText(weather.mainData.temperature),
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h1
            )
        }
    }
}

@Preview
@Composable
fun MainScreenContentPreview_Success() {
    WeatherFragmentScreenContent(
        weatherState = WeatherViewState(
            weather = PersistedWeather(
                weatherList = listOf(
                    Weather(
                        weatherId = WeatherId.BROKEN_CLOUDS,
                        title = "Clouds",
                        description = "oblačno",
                        iconId = "04d"
                    )
                ),
                locationName = "Kouřim",
                mainData = WeatherData.MainData(
                    temperature = 15.celsius,
                    pressure = 1019.hpa,
                    humidity = 38.percent
                ),
                wind = WeatherData.Wind(velocity = 15.27.mps, angle = 277.deg),
                rain = WeatherData.Rain(oneHour = 0.58.mm),
                sys = WeatherData.Sys(
                    sunrise = Instant.fromEpochSeconds(1657681500),
                    sunset = Instant.fromEpochSeconds(1657739161),
                )
            ),
            lastUpdated = 5.minutes
        )
    )
}
