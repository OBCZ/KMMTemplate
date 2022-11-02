package com.baarton.runweather.model.viewmodel

import com.baarton.runweather.model.Temperature.Companion.celsius
import com.baarton.runweather.model.Temperature.Companion.kelvin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RunnersInfoTest {

    @Test
    fun headCoverValidTest() {
        val temp = 8.3f.celsius

        assertEquals(
            RunnersInfo.HeadCover.HeadCoverHint.CAP,
            RunnersInfo.HeadCover.slow(temp)
        )
        assertEquals(
            RunnersInfo.HeadCover.HeadCoverHint.EARS,
            RunnersInfo.HeadCover.fast(temp)
        )
    }

    @Test
    fun headCoverInvalidTest() {
        val temp = (-8.3f).kelvin

        assertFailsWith(IllegalArgumentException::class) {
            RunnersInfo.HeadCover.slow(temp)
        }
        assertFailsWith(IllegalArgumentException::class) {
            RunnersInfo.HeadCover.fast(temp)
        }
    }

    @Test
    fun neckCoverValidTest() {
        val temp = 5.3f.celsius

        assertEquals(
            RunnersInfo.NeckCover.NeckCoverHint.STRONG,
            RunnersInfo.NeckCover.slow(temp)
        )
        assertEquals(
            RunnersInfo.NeckCover.NeckCoverHint.WEAK,
            RunnersInfo.NeckCover.fast(temp)
        )
    }

    @Test
    fun neckCoverInvalidTest() {
        val temp = (-8.3f).kelvin

        assertFailsWith(IllegalArgumentException::class) {
            RunnersInfo.NeckCover.slow(temp)
        }
        assertFailsWith(IllegalArgumentException::class) {
            RunnersInfo.NeckCover.fast(temp)
        }
    }

    @Test
    fun glovesValidTest() {
        val temp = 4.0f.celsius

        assertEquals(
            RunnersInfo.Gloves.GlovesHint.YES,
            RunnersInfo.Gloves.slow(temp)
        )
        assertEquals(
            RunnersInfo.Gloves.GlovesHint.NO,
            RunnersInfo.Gloves.fast(temp)
        )
    }

    @Test
    fun glovesInvalidTest() {
        val temp = (-8.3f).kelvin

        assertFailsWith(IllegalArgumentException::class) {
            RunnersInfo.Gloves.slow(temp)
        }
        assertFailsWith(IllegalArgumentException::class) {
            RunnersInfo.Gloves.fast(temp)
        }
    }

    @Test
    fun layersTopValidTest() {
        val temp = 9.0f.celsius

        assertEquals(
            RunnersInfo.LayersTop.LayersTopHint.THREE,
            RunnersInfo.LayersTop.slow(temp)
        )
        assertEquals(
            RunnersInfo.LayersTop.LayersTopHint.TWO,
            RunnersInfo.LayersTop.fast(temp)
        )
    }

    @Test
    fun layersTopInvalidTest() {
        val temp = (-8.3f).kelvin

        assertFailsWith(IllegalArgumentException::class) {
            RunnersInfo.LayersTop.slow(temp)
        }
        assertFailsWith(IllegalArgumentException::class) {
            RunnersInfo.LayersTop.fast(temp)
        }
    }

    @Test
    fun layersBottomValidTest() {
        val temp = (-0.5f).celsius

        assertEquals(
            RunnersInfo.LayersBottom.LayersBottomHint.LONG_SLEEVED_DOUBLE,
            RunnersInfo.LayersBottom.slow(temp)
        )
        assertEquals(
            RunnersInfo.LayersBottom.LayersBottomHint.LONG_SLEEVED,
            RunnersInfo.LayersBottom.fast(temp)
        )
    }

    @Test
    fun layersBottomInvalidTest() {
        val temp = (-8.3f).kelvin

        assertFailsWith(IllegalArgumentException::class) {
            RunnersInfo.LayersBottom.slow(temp)
        }
        assertFailsWith(IllegalArgumentException::class) {
            RunnersInfo.LayersBottom.fast(temp)
        }
    }

    @Test
    fun socksValidTest() {
        val temp = (-2.5f).celsius

        assertEquals(
            RunnersInfo.Socks.SocksHint.WARM,
            RunnersInfo.Socks.slow(temp)
        )
        assertEquals(
            RunnersInfo.Socks.SocksHint.NORMAL,
            RunnersInfo.Socks.fast(temp)
        )
    }

    @Test
    fun socksInvalidTest() {
        val temp = (-8.3f).kelvin

        assertFailsWith(IllegalArgumentException::class) {
            RunnersInfo.Socks.slow(temp)
        }
        assertFailsWith(IllegalArgumentException::class) {
            RunnersInfo.Socks.fast(temp)
        }
    }

    @Test
    fun sunglassesValidTest() {
        //TEST clock mock?
    }

    @Test
    fun sunglassesInvalidTest() {
        //TEST clock mock?
    }

    @Test
    fun temperatureWarningValidTest() {
        //TEST
    }

    @Test
    fun temperatureWarningInvalidTest() {
        //TEST
    }

    @Test
    fun windWarningValidTest() {
        //TEST
    }

    @Test
    fun windWarningInvalidTest() {
        //TEST
    }
}