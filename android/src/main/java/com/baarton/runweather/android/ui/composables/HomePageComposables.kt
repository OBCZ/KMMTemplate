package com.baarton.runweather.android.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import coil.compose.AsyncImage
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
import com.baarton.runweather.model.viewmodel.WeatherViewModel.Companion.getImageUrl
import com.baarton.runweather.model.viewmodel.WeatherViewModel.Companion.lastUpdatedResId
import com.baarton.runweather.model.viewmodel.WeatherViewState
import com.baarton.runweather.model.viewmodel.convert
import com.baarton.runweather.model.weather.Weather
import com.baarton.runweather.model.weather.WeatherData
import com.baarton.runweather.model.weather.WeatherId
import com.baarton.runweather.res.SharedRes
import com.baarton.runweather.ui.Vector
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
fun WeatherScreen(
) {
    val viewModel = koinViewModel<WeatherViewModel>()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareWeatherFlow = remember(viewModel.weatherState, lifecycleOwner) {
        viewModel.weatherState.flowWithLifecycle(lifecycleOwner.lifecycle)
    }

    val weatherState by lifecycleAwareWeatherFlow.collectAsState(viewModel.weatherState.value)

    WeatherScreenContent(
        weatherState = weatherState,
        onRefresh = { viewModel.refreshWeather() },
    )
}

@Composable
private fun WeatherScreenContent(
    weatherState: WeatherViewState,
    onRefresh: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        //UPGRADE change images according to the weather conditions
        Column(
            modifier = Modifier
                .paint(
                    painter = painterResource(SharedRes.images.beautiful_sunset.drawableResId),
                    contentScale = ContentScale.Crop
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            with(weatherState) {
                val isLoading = this.isLoading
                val error = this.error
                val weather = this.weather
                when {
                    isLoading -> LoadingScreen()
                    error == null && weather == null -> ErrorScreen(WeatherViewState.ErrorType.INIT_STATE, onRefresh)
                    error == null && weather != null -> WeatherScreen(copy(weather = weather))
                    error != null && weather == null -> ErrorScreen(error, onRefresh)
                    else -> ErrorScreen(WeatherViewState.ErrorType.UNKNOWN, onRefresh)
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    LineScaleProgressIndicator(
        modifier = Modifier
            .padding(4.dp),
        lineCount = 9
    )
}

@Composable
private fun ErrorScreen(error: WeatherViewState.ErrorType, onRefresh: () -> Unit) {
    Text(
        modifier = Modifier
            .padding(8.dp),
        text = stringResource(id = error.messageRes.resourceId),
        style = MaterialTheme.typography.body1,
        color = MaterialTheme.colors.onBackground
    )

    Button(onClick = onRefresh) {
        Text(
            modifier = Modifier
                .padding(2.dp),
            text = stringResource(SharedRes.strings.weather_results_refresh_button.resourceId),
            style = MaterialTheme.typography.button
        )
    }
}

@Composable
private fun ColumnScope.WeatherScreen(weatherState: WeatherViewState) {
    val weather = weatherState.weather!!.convert(weatherState.unitSetting) // we should not get NPE here
    val locationAvailable = weatherState.locationAvailable
    val networkAvailable = weatherState.networkAvailable
    val lastUpdated = weatherState.lastUpdated

    StateRow(
        weight = 1f,
        locationAvailable = locationAvailable,
        networkAvailable = networkAvailable,
        lastUpdated = lastUpdated
    )

    DataRow(
        weight = 6f,
        weather = weather
    )

    WarningRow(
        weight = 2f,
        weather = weather,
    )

    MainRow(
        weight = 3f,
        weather = weather
    )
}

@Composable
private fun ColumnScope.StateRow(weight: Float, locationAvailable: Boolean, networkAvailable: Boolean, lastUpdated: Duration?) {
    Row(
        modifier = Modifier
            .align(CenterHorizontally)
            .padding(2.dp)
            .weight(weight)
            .fillMaxWidth(),
    ) {
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
                id = SharedRes.strings.weather_location_content_description.resourceId,
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
                id = SharedRes.strings.weather_network_content_description.resourceId,
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
        id = SharedRes.strings.weather_last_updated_text.resourceId,
        formatArgs = arrayOf(
            lastUpdatedValue
        )
    )
}

@Composable
private fun ColumnScope.DataRow(weight: Float, weather: PersistedWeather) {
    Column(
        modifier = Modifier
            .align(CenterHorizontally)
            .padding(6.dp)
            .weight(weight)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(2.dp)
                .weight(1f)
                .fillMaxWidth()
        ) {
            with(weather) {
                WeatherDataColumn(Vector.HUMIDITY, dataText(mainData.humidity))
                WeatherDataColumn(Vector.RAIN, rainfallText(rain.oneHour, rain.threeHour))
                WeatherDataColumn(Vector.PRESSURE, dataText(mainData.pressure))
                WeatherDataColumn(Vector.DIRECTION, windDirectionText(wind.angle))
                WeatherDataColumn(Vector.WIND, dataText(wind.velocity))
                WeatherDataColumn(Vector.SUNRISE, timeText(sys.sunrise))
                WeatherDataColumn(Vector.SUNSET, timeText(sys.sunset))
            }
        }

        Column(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(4.dp)
                .weight(4f)
                .fillMaxSize()

        ) {

            //TODO header row?

            InfoRow(
                weather,
                SharedRes.strings.weather_runners_info_data_head_cover_category,
                RunnersInfo.HeadCover
            )
            InfoRow(
                weather,
                SharedRes.strings.weather_runners_info_data_sunglasses_category,
                RunnersInfo.Sunglasses
            )
            InfoRow(
                weather,
                SharedRes.strings.weather_runners_info_data_neck_cover_category,
                RunnersInfo.NeckCover
            )
            InfoRow(
                weather,
                SharedRes.strings.weather_runners_info_data_top_layers_category,
                RunnersInfo.LayersTop
            )
            InfoRow(
                weather,
                SharedRes.strings.weather_runners_info_data_gloves_category,
                RunnersInfo.Gloves
            )
            InfoRow(
                weather,
                SharedRes.strings.weather_runners_info_data_bottom_layers_category,
                RunnersInfo.LayersBottom
            )
            InfoRow(
                weather,
                SharedRes.strings.weather_runners_info_data_socks_category,
                RunnersInfo.Socks
            )

        }

    }
}

@Composable
private fun RowScope.WeatherDataColumn(vector: Vector, dataText: String) {
    Column(
        modifier = Modifier
            .align(CenterVertically)
            .weight(1f)
            .fillMaxWidth(),
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
private fun ColumnScope.InfoRow(weatherData: PersistedWeather, categoryTextRes: StringResource, hint: RunnersHint) {
    val slowColumnHint = when (hint) {
        is TemperatureHint -> hint.slow(weatherData.mainData.temperature)
        is WeatherHint -> hint.hint(weatherData)
    }

    val fastColumnHint = when (hint) {
        is TemperatureHint -> hint.fast(weatherData.mainData.temperature)
        is WeatherHint -> hint.hint(weatherData)
    }

    Row(
        Modifier
            .align(CenterHorizontally)
            .weight(1f)
            .fillMaxSize()
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
private fun ColumnScope.WarningRow(weight: Float, weather: PersistedWeather) {
    val temperatureWarning = RunnersInfo.TemperatureWarning.warning(weather)
    val windWarning = RunnersInfo.WindWarning.warning(weather)

    Row(
        modifier = Modifier
            .weight(weight)
            .align(CenterHorizontally)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = CenterVertically
    ) {
        temperatureWarning?.let { WarningItem(it, Vector.THERMOSTAT) }
        windWarning?.let { WarningItem(it, Vector.WIND) }
    }
}

@Composable
private fun RowScope.WarningItem(warningHint: WarningHint, warningVector: Vector) {
    Box(
        modifier = Modifier
            .weight(1f),
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
private fun ColumnScope.MainRow(weight: Float, weather: PersistedWeather) {
    Row(
        modifier = Modifier
            .align(CenterHorizontally)
            .padding(6.dp)
            .weight(weight)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(3f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = weather.locationName,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h2
            )
            Row(
                modifier = Modifier
                    .wrapContentSize(),
                verticalAlignment = CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .background(color = MaterialTheme.colors.secondary, shape = MaterialTheme.shapes.medium),
                    model = getImageUrl(weather.weatherList[0].iconId),
                    placeholder = rememberVectorPainter(image = Vector.LOADING.build()),
                    onError = { /* TODO make invisible */ },
                    onSuccess = { /* TODO make visible */ },
                    alignment = Center,
                    contentScale = ContentScale.Fit,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 6.dp),
                    text = weather.weatherList[0].description,
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Start
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
                modifier = Modifier.fillMaxWidth(),
                text = dataText(weather.mainData.temperature),
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h1,
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview
@Composable
fun MainScreenContentPreview() {
    WeatherScreenContent(
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
    ) { }
}

@Preview
@Composable
fun LoadingScreenPreview() {
    WeatherScreenContent(
        weatherState = WeatherViewState(isLoading = true)
    ) { }
}

@Preview
@Composable
fun ErrorScreenPreview() {
    WeatherScreenContent(
        weatherState = WeatherViewState(error = WeatherViewState.ErrorType.DATA_CONSISTENCY)
    ) { }
}
