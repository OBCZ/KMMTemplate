package com.baarton.runweather

import com.baarton.runweather.db.RunWeatherDb
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver


internal actual fun testDbConnection(): SqlDriver {
    return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        .also { RunWeatherDb.Schema.create(it) }
}