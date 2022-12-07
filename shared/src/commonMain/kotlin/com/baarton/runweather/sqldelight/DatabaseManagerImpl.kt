package com.baarton.runweather.sqldelight

import co.touchlab.kermit.Logger
import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.db.RunWeatherDb
import com.baarton.runweather.model.Angle.Companion.deg
import com.baarton.runweather.model.Height.Companion.mm
import com.baarton.runweather.model.Humidity.Companion.percent
import com.baarton.runweather.model.Pressure.Companion.hpa
import com.baarton.runweather.model.Temperature.Companion.kelvin
import com.baarton.runweather.model.Velocity.Companion.mps
import com.baarton.runweather.model.weather.Weather
import com.baarton.runweather.model.weather.WeatherData
import com.baarton.runweather.model.weather.WeatherId
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class DatabaseManagerImpl(
    sqlDriver: SqlDriver,
    private val backgroundDispatcher: CoroutineDispatcher,
    private val log: Logger
) : DatabaseManager {

    companion object {
        private const val ITEM_DECODING_DELIMITER = "␝" //Group Separator
        private const val DATA_DECODING_DELIMITER = "␟" //Unit Separator
    }

    private val dbRef: RunWeatherDb =
        RunWeatherDb(
            sqlDriver, PersistedWeatherAdapter = PersistedWeather.Adapter(

                weatherListAdapter = object : ColumnAdapter<List<Weather>, String> {

                    override fun encode(value: List<Weather>): String {
                        return value.joinToString(separator = ITEM_DECODING_DELIMITER) {
                            it.weatherId.id.plus(DATA_DECODING_DELIMITER).plus(it.title).plus(DATA_DECODING_DELIMITER)
                                .plus(it.description).plus(DATA_DECODING_DELIMITER).plus(it.iconId)
                        }
                    }

                    override fun decode(databaseValue: String): List<Weather> {
                        return if (databaseValue.isEmpty()) {
                            emptyList()
                        } else {
                            val result = mutableListOf<Weather>()
                            databaseValue.split(ITEM_DECODING_DELIMITER).forEach {
                                val elementSplit = it.split(DATA_DECODING_DELIMITER)
                                result.add(
                                    Weather(
                                        WeatherId.safeValueOf(elementSplit[0]),
                                        elementSplit[1],
                                        elementSplit[2],
                                        elementSplit[3]
                                    )
                                )
                            }
                            result
                        }
                    }
                },

                mainDataAdapter = object : ColumnAdapter<WeatherData.MainData, String> {

                    override fun encode(value: WeatherData.MainData): String {
                        return with(value) {
                            "${temperature.kelvin.value}${DATA_DECODING_DELIMITER}" +
                                "${pressure.hpa.value}${DATA_DECODING_DELIMITER}" +
                                "${humidity.percent.value}"
                        }
                    }

                    override fun decode(databaseValue: String): WeatherData.MainData {
                        return if (databaseValue.isEmpty()) {
                            WeatherData.MainData(293.kelvin, 1013.hpa, 0.percent)
                        } else {
                            val split = databaseValue.split(DATA_DECODING_DELIMITER)
                            WeatherData.MainData(split[0].toFloat().kelvin, split[1].toFloat().hpa, split[2].toFloat().percent)
                        }
                    }
                },

                rainAdapter = object : ColumnAdapter<WeatherData.Rain, String> {

                    override fun encode(value: WeatherData.Rain): String {
                        return with(value) {
                            "${oneHour.mm.value}${DATA_DECODING_DELIMITER}" +
                                "${threeHour.mm.value}"
                        }
                    }

                    override fun decode(databaseValue: String): WeatherData.Rain {
                        return if (databaseValue.isEmpty()) {
                            WeatherData.Rain()
                        } else {
                            val split = databaseValue.split(DATA_DECODING_DELIMITER)
                            WeatherData.Rain(split[0].toFloat().mm, split[1].toFloat().mm)
                        }
                    }
                },

                sysAdapter = object : ColumnAdapter<WeatherData.Sys, String> {

                    override fun encode(value: WeatherData.Sys): String {
                        return with(value) {
                            "${sunrise}${DATA_DECODING_DELIMITER}${sunset}"
                        }
                    }

                    override fun decode(databaseValue: String): WeatherData.Sys {
                        return if (databaseValue.isEmpty()) {
                            WeatherData.Sys(Clock.System.now(), Clock.System.now())
                        } else {
                            val split = databaseValue.split(DATA_DECODING_DELIMITER)
                            WeatherData.Sys(Instant.parse(split[0]), Instant.parse(split[1]))
                        }
                    }
                },

                windAdapter = object : ColumnAdapter<WeatherData.Wind, String> {

                    override fun encode(value: WeatherData.Wind): String {
                        return with(value) {
                            "${velocity.mps.value}${DATA_DECODING_DELIMITER}" +
                                "${angle.deg.value}"
                        }
                    }

                    override fun decode(databaseValue: String): WeatherData.Wind {
                        return if (databaseValue.isEmpty()) {
                            WeatherData.Wind(0.mps, 0.deg)
                        } else {
                            val split = databaseValue.split(DATA_DECODING_DELIMITER)
                            WeatherData.Wind(split[0].toFloat().mps, split[1].toFloat().deg)
                        }
                    }
                }
            )
        )

    override fun getAll(): List<PersistedWeather> =
        dbRef.tableQueries
            .getAll()
            .executeAsList()

    override suspend fun insert(weatherData: WeatherData) {
        log.d { "Inserting weather for ${weatherData.locationName} into database." }
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.tableQueries.nuke()
            with(weatherData) {
                dbRef.tableQueries.insert(weatherList, locationName, mainData, wind, rain, sys)
            }
        }
    }

    override suspend fun nuke() {
        log.i { "Database cleared." }
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.tableQueries.nuke()
        }
    }
}