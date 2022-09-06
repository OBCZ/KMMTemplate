package com.baarton.runweather.ktor

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.LoggerConfig
import co.touchlab.kermit.Severity
import com.baarton.runweather.models.weather.Weather
import com.baarton.runweather.models.weather.WeatherData
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class WeatherApiTest {
    private val emptyLogger = Logger(
        config = object : LoggerConfig {
            override val logWriterList: List<LogWriter> = emptyList()
            override val minSeverity: Severity = Severity.Assert
        },
        tag = ""
    )

    @Test
    fun success() = runTest {
        val engine = MockEngine {
            assertEquals(
                "https://api.openweathermap.org/data/2.5/weather?appid=b0719071a899e4b1c350725d752ec252&units=standard&lang=cz&lat=50.0&lon=15.0",
                it.url.toString()
            )
            respond(
                content = """{
    "coord": {
        "lon": 15,
        "lat": 50
    },
    "weather": [{
        "id": 804,
        "main": "Clouds",
        "description": "zataženo",
        "icon": "04d"
    }],
    "base": "stations",
    "main": {
        "temp": 300.93,
        "feels_like": 301.2,
        "temp_min": 299.38,
        "temp_max": 302.67,
        "pressure": 1009,
        "humidity": 48,
        "sea_level": 1009,
        "grnd_level": 980
    },
    "visibility": 10000,
    "wind": {
        "speed": 4.06,
        "deg": 302,
        "gust": 2.42
    },
    "clouds": {
        "all": 100
    },
    "dt": 1660918359,
    "sys": {
        "type": 1,
        "id": 6833,
        "country": "CZ",
        "sunrise": 1660881327,
        "sunset": 1660932734
    },
    "timezone": 7200,
    "id": 3073084,
    "name": "Kouřim",
    "cod": 200
}""",
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json.toString()
                )
            )
        }
        val weatherApi = WeatherApiImpl(emptyLogger, engine)

        val result = weatherApi.getJsonFromApi()
        assertEquals(
            WeatherData(
                listOf(Weather("804", "Clouds", "zataženo", "04d")),
                "Kouřim",
                WeatherData.MainData("300.93", "1009", "48"),
                WeatherData.Wind("4.06", "302"),
                null,
                WeatherData.Sys("1660881327", "1660932734")
            ),
            result
        )
    }

    @Test
    fun failure() = runTest {
        val engine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.NotFound
            )
        }
        val weatherApi = WeatherApiImpl(emptyLogger, engine)

        assertFailsWith<ClientRequestException> {
            weatherApi.getJsonFromApi()
        }
    }

}