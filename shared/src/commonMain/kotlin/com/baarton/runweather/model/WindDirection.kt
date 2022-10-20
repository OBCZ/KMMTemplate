package com.baarton.runweather.model

import com.baarton.runweather.res.SharedRes
import dev.icerock.moko.resources.StringResource


enum class WindDirection(private val signRes: StringResource, private val degIntervals: List<ClosedFloatingPointRange<Float>>) {

    NORTH(SharedRes.strings.wind_direction_north, listOf((0.0f..22.499f), (337.5f..360f))),
    NORTHEAST(SharedRes.strings.wind_direction_northeast, listOf(22.5f..67.499f)),
    EAST(SharedRes.strings.wind_direction_east, listOf(67.5f..112.499f)),
    SOUTHEAST(SharedRes.strings.wind_direction_southeast, listOf(112.5f..157.499f)),
    SOUTH(SharedRes.strings.wind_direction_south, listOf(157.5f..202.499f)),
    SOUTHWEST(SharedRes.strings.wind_direction_southwest, listOf(202.5f..247.499f)),
    WEST(SharedRes.strings.wind_direction_west, listOf(247.5f..292.499f)),
    NORTHWEST(SharedRes.strings.wind_direction_northwest, listOf(292.5f..337.499f));

    companion object {
        fun signRes(deg: Float): StringResource {
            return values().first { it.degIntervals.any { interval -> interval.contains(deg) } }.signRes
        }
    }

}