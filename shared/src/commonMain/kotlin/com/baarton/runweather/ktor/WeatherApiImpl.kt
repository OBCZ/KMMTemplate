package com.baarton.runweather.ktor

import co.touchlab.stately.ensureNeverFrozen
import com.baarton.runweather.models.weather.WeatherData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.plugins.logging.Logger as KtorLogger

class WeatherApiImpl(private val log: KermitLogger, engine: HttpClientEngine) : WeatherApi {

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

    override suspend fun getJsonFromApi(): WeatherData {
        log.d { "Get current weather data from OpenWeather API." }
        return client.get {
            weather()
        }.body()
    }

    private fun HttpRequestBuilder.weather() {
        url {
            protocol = URLProtocol.HTTPS
            host = "api.openweathermap.org"
            path("data", "2.5", "weather")
            parameters.append("appid", "b0719071a899e4b1c350725d752ec252") //FIXME hide
            parameters.append("units", "standard")
            parameters.append("lang", "cz")
            parameters.append("lat", "50.0") //TODO location manager
            parameters.append("lon", "15.0") //TODO location manager
        }
    }

    // https://api.openweathermap.org/data/2.5/weather?appid=b0719071a899e4b1c350725d752ec252&lang=cz&units=standard&lat=50.0&lon=15.0

    // https://api.openweathermap.org/data/2.5/onecall?appid=b0719071a899e4b1c350725d752ec252&lat=50&lon=15&exclude=minutely,hourly,daily,alerts
    // https://api.openweathermap.org/data/2.5/weather?appid=b0719071a899e4b1c350725d752ec252&lat=50&lon=15


}
