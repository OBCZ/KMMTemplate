package com.baarton.runweather

import co.touchlab.kermit.Logger
import com.baarton.runweather.mock.BRNO1
import com.baarton.runweather.mock.BRNO2
import com.baarton.runweather.mock.BRNO3
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

    // private suspend fun DatabaseHelper.insertBreed(name: String) {
    //     insert(listOf(name))
    // }

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
            weatherList.find { it.locationName == "Brno" },
            "Could not retrieve Weather"
        )
    }

    // @Test
    // fun `Select Item by Id Success`() = runTest {
    //     val weatherList = dbHelper.getAll().first()
    //     val firstWeather = weatherList.first()
    //     assertNotNull(
    //         dbHelper.selectById(firstWeather.id),
    //         "Could not retrieve Breed by Id"
    //     )
    // }

    // @Test
    // fun `Update Favorite Success`() = runTest {
    //     val breeds = dbHelper.getAll().first()
    //     val firstBreed = breeds.first()
    //     dbHelper.updateFavorite(firstBreed.id, true)
    //     val newBreed = dbHelper.selectById(firstBreed.id).first().first()
    //     assertNotNull(
    //         newBreed,
    //         "Could not retrieve Breed by Id"
    //     )
    //     assertTrue(
    //         newBreed.favorite,
    //         "Favorite Did Not Save"
    //     )
    // }

    @Test
    fun `Delete All Success`() = runTest {
        dbHelper.insert(BRNO2.get())
        dbHelper.insert(BRNO3.get())
        assertTrue(dbHelper.getAll().first().isNotEmpty())
        dbHelper.nuke()

        assertTrue(
            dbHelper.getAll().first().isEmpty(),
            "Delete All did not work"
        )
    }
}
