package com.baarton.runweather.model

import com.baarton.runweather.model.Angle.Companion.deg
import com.baarton.runweather.res.SharedRes
import kotlin.test.Test
import kotlin.test.assertEquals


class WindDirectionTest {

    private val testParams by lazy {
        listOf(
            Pair(0f.deg, SharedRes.strings.wind_direction_north),
            Pair(45f.deg, SharedRes.strings.wind_direction_northeast),
            Pair(90f.deg, SharedRes.strings.wind_direction_east),
            Pair(135f.deg, SharedRes.strings.wind_direction_southeast),
            Pair(180f.deg, SharedRes.strings.wind_direction_south),
            Pair(225f.deg, SharedRes.strings.wind_direction_southwest),
            Pair(270f.deg, SharedRes.strings.wind_direction_west),
            Pair(315f.deg, SharedRes.strings.wind_direction_northwest),
            Pair(360f.deg, SharedRes.strings.wind_direction_north),
            Pair(21f.deg, SharedRes.strings.wind_direction_north),
            Pair(57f.deg, SharedRes.strings.wind_direction_northeast),
            Pair(100f.deg, SharedRes.strings.wind_direction_east),
            Pair(157f.deg, SharedRes.strings.wind_direction_southeast),
            Pair(158f.deg, SharedRes.strings.wind_direction_south),
            Pair(247f.deg, SharedRes.strings.wind_direction_southwest),
            Pair(248f.deg, SharedRes.strings.wind_direction_west),
            Pair(337f.deg, SharedRes.strings.wind_direction_northwest)
        )
    }

    @Test
    fun windDirectionEnumTest() {
        testParams.forEach {
            val expected = it.second
            val actual = WindDirection.signRes(it.first)
            assertEquals(
                expected,
                actual,
                "The wind direction is not as expected. Actual result: >>> $actual <<<; Expected result: >>> $expected <<<."
            )
        }
    }
}