package com.baarton.runweather.model

import com.baarton.runweather.res.SharedRes
import kotlin.test.Test
import kotlin.test.assertEquals


class WindDirectionTest {

    private val testParams by lazy {
        listOf(
            Pair(0f, SharedRes.strings.wind_direction_north),
            Pair(45f, SharedRes.strings.wind_direction_northeast),
            Pair(90f, SharedRes.strings.wind_direction_east),
            Pair(135f, SharedRes.strings.wind_direction_southeast),
            Pair(180f, SharedRes.strings.wind_direction_south),
            Pair(225f, SharedRes.strings.wind_direction_southwest),
            Pair(270f, SharedRes.strings.wind_direction_west),
            Pair(315f, SharedRes.strings.wind_direction_northwest),
            Pair(360f, SharedRes.strings.wind_direction_north),
            Pair(21f, SharedRes.strings.wind_direction_north),
            Pair(57f, SharedRes.strings.wind_direction_northeast),
            Pair(100f, SharedRes.strings.wind_direction_east),
            Pair(157f, SharedRes.strings.wind_direction_southeast),
            Pair(158f, SharedRes.strings.wind_direction_south),
            Pair(247f, SharedRes.strings.wind_direction_southwest),
            Pair(248f, SharedRes.strings.wind_direction_west),
            Pair(337f, SharedRes.strings.wind_direction_northwest)
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