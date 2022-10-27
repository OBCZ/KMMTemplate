package com.baarton.runweather.sqldelight

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.LoggerConfig
import co.touchlab.kermit.Severity
import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.mock.BRNO1
import com.baarton.runweather.mock.BRNO2
import com.baarton.runweather.mock.BRNO3
import com.baarton.runweather.mock.BRNO4
import com.baarton.runweather.testDbConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import com.baarton.runweather.model.weather.WeatherId.*
import kotlinx.datetime.Instant

class SqlDelightTest {

    private lateinit var dbHelper: DatabaseHelper

    private val emptyLogger = Logger(
        config = object : LoggerConfig {
            override val logWriterList: List<LogWriter> = emptyList()
            override val minSeverity: Severity = Severity.Assert
        },
        tag = ""
    )

    @BeforeTest
    fun setup() = runTest {
        dbHelper = DatabaseHelper(
            testDbConnection(),
            emptyLogger,
            Dispatchers.Default
        )
        dbHelper.nuke()
        dbHelper.insert(BRNO1.data)
    }

    @Test
    fun `Select first from all items`() = runTest {
        val firstItem = dbHelper.getAll().first()
        assertNotNull(firstItem, "Could not retrieve Weather")

        with(firstItem) {
            assertTrue { locationName == "Brno1" }
            assertTrue { weatherList.size == 1 }
            assertTrue { weatherList[0].description == "clear sky" }
            assertTrue { weatherList[0].weatherId == CLEAR_SKY }
            assertTrue { weatherList[0].title == "Clear" }
            assertTrue { weatherList[0].iconId == "01d" }
            assertTrue { mainData.pressure == "1021" }
            assertTrue { mainData.humidity == "45" }
            assertTrue { mainData.temperature == "265.90" }
            assertTrue { rain == null }
            assertTrue { sys.sunrise == Instant.fromEpochSeconds(1646803774) }
            assertTrue { sys.sunset == Instant.fromEpochSeconds(1646844989) }
        }
    }

    @Test
    fun `Select first from all with Rain`() = runTest {
        dbHelper.insert(BRNO4.data)
        val firstItem = dbHelper.getAll().first()
        assertNotNull(firstItem, "Could not retrieve Weather")

        with(firstItem) {
            assertTrue { locationName == "Brno Rain" }
            assertTrue { weatherList.size == 2 }
            assertTrue { weatherList[0].description == "heavy rain" }
            assertTrue { weatherList[0].weatherId == HEAVY_INTENSITY_RAIN }
            assertTrue { weatherList[0].title == "Rain" }
            assertTrue { weatherList[0].iconId == "05d" }
            assertTrue { weatherList[1].description == "light rain" }
            assertTrue { weatherList[1].weatherId == LIGHT_RAIN }
            assertTrue { weatherList[1].title == "Light Rain" }
            assertTrue { weatherList[1].iconId == "08d" }
            assertTrue { mainData.pressure == "1020" }
            assertTrue { mainData.humidity == "35" }
            assertTrue { mainData.temperature == "268.90" }
            assertTrue { rain!!.oneHour == "1" }
            assertTrue { rain!!.threeHour == "3" }
            assertTrue { sys.sunrise == Instant.fromEpochSeconds(1646800774) }
            assertTrue { sys.sunset == Instant.fromEpochSeconds(1646849989) }
        }
    }

    @Test
    fun `Delete all`() = runTest {
        dbHelper.insert(BRNO2.data)
        dbHelper.insert(BRNO3.data)

        assertEquals(
            dbHelper.getAll().first(),
            with(BRNO3.data) {
                PersistedWeather(
                    weatherList,
                    locationName,
                    mainData,
                    wind,
                    rain,
                    sys
                )
            })

        dbHelper.nuke()

        assertTrue(
            dbHelper.getAll().isEmpty(),
            "Delete All did not work"
        )
    }

}