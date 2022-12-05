package com.baarton.runweather

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.LoggerConfig
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import com.baarton.runweather.sensor.location.Location
import com.squareup.sqldelight.db.SqlDriver
import kotlin.random.Random

internal expect fun testDbConnection(): SqlDriver

fun emptyLogger(): Logger = Logger(
    config = object : LoggerConfig {
        override val logWriterList: List<LogWriter> = emptyList()
        override val minSeverity: Severity = Severity.Assert
    },
    tag = APP_TEST_TAG
)

fun testLogger(): Logger = Logger(StaticConfig(), APP_TEST_TAG)

fun randomLocation(): Location {
    val rand = Random(1)
    return Location(rand.nextDouble(0.0, 180.0), rand.nextDouble(0.0, 180.0))
}
