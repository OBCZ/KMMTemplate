package com.baarton.runweather

import co.touchlab.kermit.Logger
import com.baarton.runweather.mock.BRNO1
import com.baarton.runweather.mock.BRNO2
import com.baarton.runweather.mock.BRNO3
import com.baarton.runweather.sqldelight.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class SqlDelightTest {

    private lateinit var dbHelper: DatabaseHelper

    @BeforeTest
    fun setup() = runTest {
        dbHelper = DatabaseHelper(
            testDbConnection(),
            Logger,
            Dispatchers.Default
        )
        dbHelper.nuke()
        dbHelper.insert(BRNO1.get())
    }

    @Test
    fun `Select All Items Success`() = runTest {
        val weatherList = dbHelper.getAll().first()
        assertNotNull(
            weatherList,
            "Could not retrieve Weather"
        )
        assertTrue { weatherList.locationName == "Brno" }
    }

    @Test
    fun `Delete All Success`() = runTest {
        dbHelper.insert(BRNO2.get())
        dbHelper.insert(BRNO3.get())
        assertTrue(dbHelper.getAll().first() != null)
        dbHelper.nuke()

        assertTrue(
            dbHelper.getAll().first() == null,
            "Delete All did not work"
        )
    }
}