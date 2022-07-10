package com.baarton.runweather

import co.touchlab.kermit.Logger
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

    private suspend fun DatabaseHelper.insertBreed(name: String) {
        insert(listOf(name))
    }

    @BeforeTest
    fun setup() = runTest {
        dbHelper = DatabaseHelper(
            testDbConnection(),
            Logger,
            Dispatchers.Default
        )
        dbHelper.nuke()
        dbHelper.insertBreed("Beagle")
    }

    @Test
    fun `Select All Items Success`() = runTest {
        val breeds = dbHelper.getAll().first()
        assertNotNull(
            breeds.find { it.name == "Beagle" },
            "Could not retrieve Breed"
        )
    }

    @Test
    fun `Select Item by Id Success`() = runTest {
        val breeds = dbHelper.getAll().first()
        val firstBreed = breeds.first()
        assertNotNull(
            dbHelper.selectById(firstBreed.id),
            "Could not retrieve Breed by Id"
        )
    }

    @Test
    fun `Update Favorite Success`() = runTest {
        val breeds = dbHelper.getAll().first()
        val firstBreed = breeds.first()
        dbHelper.updateFavorite(firstBreed.id, true)
        val newBreed = dbHelper.selectById(firstBreed.id).first().first()
        assertNotNull(
            newBreed,
            "Could not retrieve Breed by Id"
        )
        assertTrue(
            newBreed.favorite,
            "Favorite Did Not Save"
        )
    }

    @Test
    fun `Delete All Success`() = runTest {
        dbHelper.insertBreed("Poodle")
        dbHelper.insertBreed("Schnauzer")
        assertTrue(dbHelper.getAll().first().isNotEmpty())
        dbHelper.nuke()

        assertTrue(
            dbHelper.getAll().first().count() == 0,
            "Delete All did not work"
        )
    }
}
