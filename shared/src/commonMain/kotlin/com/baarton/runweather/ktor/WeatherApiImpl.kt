package com.baarton.runweather.ktor

import co.touchlab.stately.ensureNeverFrozen
import com.baarton.runweather.sensor.location.Location
import com.baarton.runweather.model.weather.WeatherData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.plugins.logging.Logger as KtorLogger

private const val WEATHER_KEY = "weather"

private const val DATA_AUTHORITY = "api.openweathermap.org"
private const val IMG_AUTHORITY = "openweathermap.org"
private const val DATA_PATH = "data"
private const val IMG_PATH = "img/wn"
private const val DATA_VERSION = "2.5"

private const val APP_ID_KEY = "appid"
private const val LATITUDE_KEY = "lat"
private const val LONGITUDE_KEY = "lon"
private const val UNITS_KEY = "units"
private const val LANGUAGE_KEY = "lang"

private const val OPENWEATHER_API_KEY_APP_ID_VALUE = "b0719071a899e4b1c350725d752ec252" //FIXME hide
private const val UNITS_VALUE = "standard" //Intended units request value constant.


class WeatherDataApiImpl(engine: HttpClientEngine, private val log: KermitLogger) : WeatherDataApi {

    private val client = HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = object : KtorLogger {
                override fun log(message: String) {
                    log.v { message }
                }
            }

            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            val timeout = 30000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
    }

    init {
        ensureNeverFrozen()
    }

    override suspend fun getWeatherFromApi(location: Location): WeatherData {
        log.d { "Get current weather data from OpenWeather API for location: $location." }
        return client.get {
            weather(location)
        }.body()
    }

    private fun HttpRequestBuilder.weather(location: Location) {
        url {
            protocol = URLProtocol.HTTPS
            host = DATA_AUTHORITY
            path(DATA_PATH, DATA_VERSION, WEATHER_KEY)
            parameters.append(APP_ID_KEY, OPENWEATHER_API_KEY_APP_ID_VALUE)
            parameters.append(UNITS_KEY, UNITS_VALUE)
            parameters.append(LANGUAGE_KEY, "cz") //TODO
            parameters.append(LATITUDE_KEY, location.latitude.toString())
            parameters.append(LONGITUDE_KEY, location.longitude.toString())
        }
    }
}

object ImageUrlBuilder : ImageDataApi {

    override fun buildUrl(imageId: String): String {
        return URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = IMG_AUTHORITY
            path(IMG_PATH, imageId.plus("@2x.png"))
        }.buildString()
    }

}
