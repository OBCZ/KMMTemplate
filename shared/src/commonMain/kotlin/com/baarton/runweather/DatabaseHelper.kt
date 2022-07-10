package com.baarton.runweather

import co.touchlab.kermit.Logger
import com.baarton.runweather.db.CurrentWeather
import com.baarton.runweather.db.RunWeatherDb
import com.baarton.runweather.models.Weather
import com.baarton.runweather.models.WeatherData
import com.baarton.runweather.sqldelight.transactionWithContext
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class DatabaseHelper(
    sqlDriver: SqlDriver,
    private val log: Logger,
    private val backgroundDispatcher: CoroutineDispatcher
) {
    private val dbRef: RunWeatherDb =
        RunWeatherDb(
            //TODO review/extract adapters and their default values
            sqlDriver, CurrentWeatherAdapter = CurrentWeather.Adapter(
                weatherListAdapter = object : ColumnAdapter<List<Weather>, String> {

                    override fun encode(value: List<Weather>): String {
                        return value.joinToString(separator = ",") {
                            it.weatherId.plus("|").plus(it.title).plus("|")
                                .plus(it.description).plus("|").plus(it.iconId)
                        }
                    }

                    override fun decode(databaseValue: String): List<Weather> {
                        return if (databaseValue.isEmpty()) {
                            emptyList()
                        } else {
                            val result = mutableListOf<Weather>()
                            databaseValue.split(",").forEach {
                                val elementSplit = it.split("|")
                                result.add(Weather(elementSplit[0], elementSplit[1], elementSplit[2], elementSplit[3]))
                            }
                            result
                        }
                    }
                },

                mainDataAdapter = object : ColumnAdapter<WeatherData.MainData, String> {

                    override fun encode(value: WeatherData.MainData): String {
                        return value.temperature.plus("|").plus(value.pressure).plus("|")
                            .plus(value.humidity)
                    }

                    override fun decode(databaseValue: String): WeatherData.MainData {
                        return if (databaseValue.isEmpty()) {
                            WeatherData.MainData("", "", "")
                        } else {
                            val split = databaseValue.split("|")
                            WeatherData.MainData(split[0], split[1], split[2])
                        }
                    }
                },
                //TODO what of rain is null? what does this do?
                rainAdapter = object : ColumnAdapter<WeatherData.Rain, String> {

                    override fun encode(value: WeatherData.Rain): String {
                        return value.oneHour.plus("|").plus(value.threeHour)
                    }

                    override fun decode(databaseValue: String): WeatherData.Rain {
                        return if (databaseValue.isEmpty()) {
                            WeatherData.Rain("", "")
                        } else {
                            val split = databaseValue.split("|")
                            WeatherData.Rain(split[0], split[1])
                        }
                    }
                },
                sysAdapter = object : ColumnAdapter<WeatherData.Sys, String> {

                    override fun encode(value: WeatherData.Sys): String {
                        return value.sunrise.plus("|").plus(value.sunset)
                    }

                    override fun decode(databaseValue: String): WeatherData.Sys {
                        return if (databaseValue.isEmpty()) {
                            WeatherData.Sys("", "")
                        } else {
                            val split = databaseValue.split("|")
                            WeatherData.Sys(split[0], split[1])
                        }
                    }
                },
                windAdapter = object : ColumnAdapter<WeatherData.Wind, String> {

                    override fun encode(value: WeatherData.Wind): String {
                        return value.speed.plus("|").plus(value.deg)
                    }

                    override fun decode(databaseValue: String): WeatherData.Wind {
                        return if (databaseValue.isEmpty()) {
                            WeatherData.Wind("", "")
                        } else {
                            val split = databaseValue.split("|")
                            WeatherData.Wind(split[0], split[1])
                        }
                    }
                }
            )

        )

    fun getAll(): Flow<List<CurrentWeather>> =
        dbRef.tableQueries
            .getAll()
            .asFlow()
            .mapToList()
            .flowOn(backgroundDispatcher)

    suspend fun insert(weatherData: WeatherData) {
        log.d { "Inserting weather for ${weatherData.locationName} into database" }
        dbRef.transactionWithContext(backgroundDispatcher) {
            // breeds.forEach { breed ->
            with(weatherData) {
                dbRef.tableQueries.insert(weatherList, locationName, mainData, wind, rain, sys)

            }
            // }
        }
    }

    // fun selectById(id: Long): Flow<List<WeatherData>> =
    //     dbRef.tableQueries
    //         .selectById(id)
    //         .asFlow()
    //         .mapToList()
    //         .flowOn(backgroundDispatcher)

    suspend fun nuke() {
        log.i { "Database Cleared" }
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.tableQueries.nuke()
        }
    }

    // suspend fun updateFavorite(breedId: Long, favorite: Boolean) {
    //     log.i { "Breed $breedId: Favorited $favorite" }
    //     dbRef.transactionWithContext(backgroundDispatcher) {
    //         dbRef.tableQueries.updateFavorite(favorite, breedId)
    //     }
    // }
}
