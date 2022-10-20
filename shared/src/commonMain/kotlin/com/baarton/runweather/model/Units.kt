package com.baarton.runweather.model

import com.baarton.runweather.res.SharedRes
import com.baarton.runweather.util.roundDecimals
import dev.icerock.moko.resources.StringResource
import kotlin.math.roundToInt


enum class MeasureUnit(
    val tempUnit: TempUnit,
    val humidityUnit: HumidityUnit,
    val rainfallUnit: RainfallUnit,
    val pressureUnit: PressureUnit,
    val windSpeedUnit: WindSpeedUnit
) {
    METRIC(TempUnit.CELSIUS, HumidityUnit.PERCENT, RainfallUnit.MILLIMETER, PressureUnit.HECTOPASCAL, WindSpeedUnit.METERS_PER_SECOND),
    IMPERIAL(TempUnit.FAHRENHEIT, HumidityUnit.PERCENT, RainfallUnit.INCH, PressureUnit.MILLIBAR, WindSpeedUnit.MILES_PER_HOUR);

    companion object {

        fun default(): MeasureUnit {
            return METRIC
        }

        fun safeValueOf(name: String): MeasureUnit {
            return try {
                valueOf(name)
            } catch (e: IllegalArgumentException) {
                default()
            }
        }
    }
}

enum class TempUnit(override val stringRes: StringResource) : DataUnit {
    KELVIN(SharedRes.strings.unit_kelvin),
    CELSIUS(SharedRes.strings.unit_celsius),
    FAHRENHEIT(SharedRes.strings.unit_fahrenheit);

    /*
     * Only values in Kelvin are expected.
     */
    override fun prepareValue(value: Float): String {
        return "${convertUnit(value).roundDecimals(1)}"
    }

    private fun convertUnit(value: Float): Float {
        return when (this) {
            KELVIN -> KELVIN.toKelvin(value)
            CELSIUS -> KELVIN.toCelsius(value)
            FAHRENHEIT -> KELVIN.toFahrenheit(value)
        }
    }

    internal fun toKelvin(value: Float): Float {
        return when (this) {
            CELSIUS -> value + 273.15f
            KELVIN -> value
            FAHRENHEIT -> ((value + 459.67f) * 5 / 9).roundDecimals(3)
        }
    }

    internal fun toCelsius(value: Float): Float {
        return when (this) {
            CELSIUS -> value
            KELVIN -> value - 273.15f
            FAHRENHEIT -> ((value - 32f) * 5 / 9).roundDecimals(3)
        }
    }

    internal fun toFahrenheit(value: Float): Float {
        return when (this) {
            CELSIUS -> ((value * 9 / 5) + 32f).roundDecimals(3)
            KELVIN -> ((value * 9 / 5) - 459.67f).roundDecimals(3)
            FAHRENHEIT -> value
        }
    }
}

enum class HumidityUnit(override val stringRes: StringResource) : DataUnit {
    PERCENT(SharedRes.strings.unit_humidity);

    override fun prepareValue(value: Float): String {
        return value.toInt().toString()
    }
}

enum class RainfallUnit(override val stringRes: StringResource) : DataUnit {
    MILLIMETER(SharedRes.strings.unit_millimeter),
    INCH(SharedRes.strings.unit_inch);

    /*
     * Only values in millimeters are expected.
     */
    override fun prepareValue(value: Float): String {
        val valueToPrint = if (value == 0f) {
            "0"
        } else {
            convertUnit(value).roundDecimals(1)
        }
        return "$valueToPrint"
    }

    private fun convertUnit(value: Float): Float {
        return when (this) {
            MILLIMETER -> MILLIMETER.toMillimeter(value)
            INCH -> MILLIMETER.toInch(value)
        }
    }

    internal fun toMillimeter(value: Float): Float {
        return when (this) {
            MILLIMETER -> value
            INCH -> (value * 25.4f).roundDecimals(3)
        }
    }

    internal fun toInch(value: Float): Float {
        return when (this) {
            MILLIMETER -> (value / 25.4f).roundDecimals(3)
            INCH -> value
        }
    }
}

enum class PressureUnit(override val stringRes: StringResource) : DataUnit {
    HECTOPASCAL(SharedRes.strings.unit_hectopascal),
    MILLIBAR(SharedRes.strings.unit_millibar);

    /*
     * Only values in hectoPascals are expected.
     */
    override fun prepareValue(value: Float): String {
        return "${convertUnit(value).roundToInt()}"
    }

    private fun convertUnit(value: Float): Float {
        return when (this) {
            HECTOPASCAL -> HECTOPASCAL.toHectoPascal(value)
            MILLIBAR -> HECTOPASCAL.toMillibar(value)
        }
    }

    internal fun toHectoPascal(value: Float): Float {
        return when (this) {
            HECTOPASCAL -> value
            MILLIBAR -> value
        }
    }

    internal fun toMillibar(value: Float): Float {
        return when (this) {
            HECTOPASCAL -> value
            MILLIBAR -> value
        }
    }
}

enum class WindSpeedUnit(override val stringRes: StringResource) : DataUnit {
    METERS_PER_SECOND(SharedRes.strings.unit_meters_per_second),
    MILES_PER_HOUR(SharedRes.strings.unit_miles_per_hour);

    /*
     * Only values in meters per second are expected.
     */
    override fun prepareValue(value: Float): String {
        val valueToPrint = if (value == 0f) {
            "0"
        } else {
            convertUnit(value).roundDecimals(1)
        }
        return "$valueToPrint"
    }

    private fun convertUnit(value: Float): Float {
        return when (this) {
            METERS_PER_SECOND -> METERS_PER_SECOND.toMetersPerSecond(value)
            MILES_PER_HOUR -> METERS_PER_SECOND.toMilesPerHour(value)
        }
    }

    internal fun toMetersPerSecond(value: Float): Float {
        return when (this) {
            METERS_PER_SECOND -> value
            MILES_PER_HOUR -> (value * 0.447f).roundDecimals(3)
        }
    }

    internal fun toMilesPerHour(value: Float): Float {
        return when (this) {
            METERS_PER_SECOND -> (value / 0.447f).roundDecimals(3)
            MILES_PER_HOUR -> value
        }
    }
}

interface DataUnit {

    val stringRes: StringResource
    fun prepareValue(value: Float): String
}
