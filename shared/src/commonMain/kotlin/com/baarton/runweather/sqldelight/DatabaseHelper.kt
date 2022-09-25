package com.baarton.runweather.sqldelight

import co.touchlab.kermit.Logger
import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.db.RunWeatherDb
import com.baarton.runweather.models.weather.Weather
import com.baarton.runweather.models.weather.WeatherData
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class DatabaseHelper(
    sqlDriver: SqlDriver,
    private val log: Logger,
    private val backgroundDispatcher: CoroutineDispatcher
) {

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
                            it.weatherId.plus(DATA_DECODING_DELIMITER).plus(it.title).plus(DATA_DECODING_DELIMITER)
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
                                        elementSplit[0],
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
                        return value.temperature.plus(DATA_DECODING_DELIMITER).plus(value.pressure).plus(DATA_DECODING_DELIMITER)
                            .plus(value.humidity)
                    }

                    override fun decode(databaseValue: String): WeatherData.MainData {
                        return if (databaseValue.isEmpty()) {
                            WeatherData.MainData("", "", "")
                        } else {
                            val split = databaseValue.split(DATA_DECODING_DELIMITER)
                            WeatherData.MainData(split[0], split[1], split[2])
                        }
                    }
                },
                rainAdapter = object : ColumnAdapter<WeatherData.Rain, String> {

                    override fun encode(value: WeatherData.Rain): String {
                        return value.oneHour.plus(DATA_DECODING_DELIMITER).plus(value.threeHour)
                    }

                    override fun decode(databaseValue: String): WeatherData.Rain {
                        return if (databaseValue.isEmpty()) {
                            WeatherData.Rain("", "")
                        } else {
                            val split = databaseValue.split(DATA_DECODING_DELIMITER)
                            WeatherData.Rain(split[0], split[1])
                        }
                    }
                },
                sysAdapter = object : ColumnAdapter<WeatherData.Sys, String> {

                    override fun encode(value: WeatherData.Sys): String {
                        return value.sunrise.plus(DATA_DECODING_DELIMITER).plus(value.sunset)
                    }

                    override fun decode(databaseValue: String): WeatherData.Sys {
                        return if (databaseValue.isEmpty()) {
                            WeatherData.Sys("", "")
                        } else {
                            val split = databaseValue.split(DATA_DECODING_DELIMITER)
                            WeatherData.Sys(split[0], split[1])
                        }
                    }
                },
                windAdapter = object : ColumnAdapter<WeatherData.Wind, String> {

                    override fun encode(value: WeatherData.Wind): String {
                        return value.speed.plus(DATA_DECODING_DELIMITER).plus(value.deg)
                    }

                    override fun decode(databaseValue: String): WeatherData.Wind {
                        return if (databaseValue.isEmpty()) {
                            WeatherData.Wind("", "")
                        } else {
                            val split = databaseValue.split(DATA_DECODING_DELIMITER)
                            WeatherData.Wind(split[0], split[1])
                        }
                    }
                }
            )
        )

    fun getAll(): Flow<PersistedWeather?> =
        dbRef.tableQueries
            .getAll()
            .asFlow()
            .mapToOneOrNull()
            .flowOn(backgroundDispatcher)

    suspend fun insert(weatherData: WeatherData) {
        log.d { "Inserting weather for ${weatherData.locationName} into database" }
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.tableQueries.nuke()
            with(weatherData) {
                dbRef.tableQueries.insert(weatherList, locationName, mainData, wind, rain, sys)
            }
        }
    }

    suspend fun nuke() {
        log.i { "Database Cleared" }
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.tableQueries.nuke()
        }
    }

}