package com.baarton.runweather.model

import com.baarton.runweather.model.Angle.Companion.deg
import com.baarton.runweather.model.Height.Companion.inch
import com.baarton.runweather.model.Height.Companion.mm
import com.baarton.runweather.model.Humidity.Companion.percent
import com.baarton.runweather.model.Pressure.Companion.hpa
import com.baarton.runweather.model.Pressure.Companion.mbar
import com.baarton.runweather.model.Temperature.Companion.celsius
import com.baarton.runweather.model.Temperature.Companion.fahrenheit
import com.baarton.runweather.model.Velocity.Companion.mph
import com.baarton.runweather.model.Velocity.Companion.mps
import com.baarton.runweather.res.SharedRes
import com.baarton.runweather.util.roundDecimals
import dev.icerock.moko.resources.StringResource
import kotlin.math.roundToInt

enum class UnitSystem(
    val tempSwitch: (Temperature) -> Temperature,
    val humiditySwitch: (Humidity) -> Humidity,
    val heightSwitch: (Height) -> Height,
    val pressureSwitch: (Pressure) -> Pressure,
    val angleSwitch: (Angle) -> Angle,
    val velocitySwitch: (Velocity) -> Velocity
) {
    METRIC({ it.celsius }, { it.percent }, { it.mm }, { it.hpa }, { it.deg }, { it.mps }),
    IMPERIAL({ it.fahrenheit }, { it.percent }, { it.inch }, { it.mbar }, { it.deg }, { it.mph });

    companion object {

        fun default(): UnitSystem {
            return METRIC
        }

        fun safeValueOf(name: String): UnitSystem {
            return try {
                valueOf(name)
            } catch (e: IllegalArgumentException) {
                default()
            }
        }
    }
}

enum class TempUnit(override val stringRes: StringResource) : MeasureUnit {
    KELVIN(SharedRes.strings.unit_kelvin),
    CELSIUS(SharedRes.strings.unit_celsius),
    FAHRENHEIT(SharedRes.strings.unit_fahrenheit);
}

data class Temperature(override val value: Float, override val unit: TempUnit) : QuantityUnit {

    companion object {

        val Temperature.celsius get() = convert(TempUnit.CELSIUS)
        val Number.celsius get() = Temperature(toFloat(), TempUnit.CELSIUS)
        val Temperature.kelvin get() = convert(TempUnit.KELVIN)
        val Number.kelvin get() = Temperature(toFloat(), TempUnit.KELVIN)
        val Temperature.fahrenheit get() = convert(TempUnit.FAHRENHEIT)
        val Number.fahrenheit get() = Temperature(toFloat(), TempUnit.FAHRENHEIT)

        private fun Temperature.convert(targetUnit: TempUnit): Temperature {
            return when (targetUnit) {
                TempUnit.KELVIN -> toKelvin()
                TempUnit.CELSIUS -> toCelsius()
                TempUnit.FAHRENHEIT -> toFahrenheit()
            }
        }

        private fun Temperature.toKelvin(): Temperature {
            return when (unit) {
                TempUnit.CELSIUS -> Temperature(value + 273.15f, TempUnit.KELVIN)
                TempUnit.KELVIN -> this
                TempUnit.FAHRENHEIT -> Temperature(((value + 459.67f) * 5 / 9).roundDecimals(3), TempUnit.KELVIN)
            }
        }

        private fun Temperature.toCelsius(): Temperature {
            return when (unit) {
                TempUnit.CELSIUS -> this
                TempUnit.KELVIN -> Temperature(value - 273.15f, TempUnit.CELSIUS)
                TempUnit.FAHRENHEIT -> Temperature(((value - 32f) * 5 / 9).roundDecimals(3), TempUnit.CELSIUS)
            }
        }

        private fun Temperature.toFahrenheit(): Temperature {
            return when (unit) {
                TempUnit.CELSIUS -> Temperature(((value * 9 / 5) + 32f).roundDecimals(3), TempUnit.FAHRENHEIT)
                TempUnit.KELVIN -> Temperature(((value * 9 / 5) - 459.67f).roundDecimals(3), TempUnit.FAHRENHEIT)
                TempUnit.FAHRENHEIT -> this
            }
        }
    }

    override fun formattedValue(): String {
        return "${value.roundDecimals(1)}"
    }

    override fun base(): QuantityUnit {
        return this.kelvin
    }
}

enum class HumidityUnit(override val stringRes: StringResource) : MeasureUnit {
    PERCENT(SharedRes.strings.unit_humidity);
}

data class Humidity(override val value: Float, override val unit: HumidityUnit) : QuantityUnit {

    companion object {

        val Humidity.percent get() = convertUnit(HumidityUnit.PERCENT)
        val Number.percent get() = Humidity(toFloat(), HumidityUnit.PERCENT)

        private fun Humidity.convertUnit(targetUnit: HumidityUnit): Humidity {
            return when (targetUnit) {
                HumidityUnit.PERCENT -> toPercent()
            }
        }

        private fun Humidity.toPercent(): Humidity {
            return when (unit) {
                HumidityUnit.PERCENT -> this
            }
        }
    }

    override fun formattedValue(): String {
        return "${value.roundToInt()}"
    }

    override fun base(): QuantityUnit {
        return this.percent
    }
}

enum class HeightUnit(override val stringRes: StringResource) : MeasureUnit {
    MILLIMETER(SharedRes.strings.unit_millimeter),
    INCH(SharedRes.strings.unit_inch);
}

data class Height(override val value: Float, override val unit: HeightUnit) : QuantityUnit {

    companion object {

        val Height.mm get() = convertUnit(HeightUnit.MILLIMETER)
        val Number.mm get() = Height(toFloat(), HeightUnit.MILLIMETER)
        val Height.inch get() = convertUnit(HeightUnit.INCH)
        val Number.inch get() = Height(toFloat(), HeightUnit.INCH)

        private fun Height.convertUnit(targetUnit: HeightUnit): Height {
            return when (targetUnit) {
                HeightUnit.MILLIMETER -> toMillimeter()
                HeightUnit.INCH -> toInch()
            }
        }

        private fun Height.toMillimeter(): Height {
            return when (unit) {
                HeightUnit.MILLIMETER -> this
                HeightUnit.INCH -> Height((value * 25.4f).roundDecimals(3), HeightUnit.MILLIMETER)
            }
        }

        private fun Height.toInch(): Height {
            return when (unit) {
                HeightUnit.MILLIMETER -> Height((value / 25.4f).roundDecimals(3), HeightUnit.INCH)
                HeightUnit.INCH -> this
            }
        }
    }

    override fun formattedValue(): String {
        return if (value == 0f) {
            "0"
        } else {
            "${value.roundDecimals(1)}"
        }
    }

    override fun base(): QuantityUnit {
        return this.mm
    }
}

enum class PressureUnit(override val stringRes: StringResource) : MeasureUnit {
    HECTOPASCAL(SharedRes.strings.unit_hectopascal),
    MILLIBAR(SharedRes.strings.unit_millibar);
}

data class Pressure(override val value: Float, override val unit: PressureUnit) : QuantityUnit {

    companion object {

        val Pressure.hpa get() = convertUnit(PressureUnit.HECTOPASCAL)
        val Number.hpa get() = Pressure(toFloat(), PressureUnit.HECTOPASCAL)
        val Pressure.mbar get() = convertUnit(PressureUnit.MILLIBAR)
        val Number.mbar get() = Pressure(toFloat(), PressureUnit.MILLIBAR)

        private fun Pressure.convertUnit(targetUnit: PressureUnit): Pressure {
            return when (targetUnit) {
                PressureUnit.HECTOPASCAL -> toHectoPascal()
                PressureUnit.MILLIBAR -> toMillibar()
            }
        }

        private fun Pressure.toHectoPascal(): Pressure {
            return when (unit) {
                PressureUnit.HECTOPASCAL -> this
                PressureUnit.MILLIBAR -> this.copy(unit = PressureUnit.HECTOPASCAL)
            }
        }

        private fun Pressure.toMillibar(): Pressure {
            return when (unit) {
                PressureUnit.HECTOPASCAL -> this.copy(unit = PressureUnit.MILLIBAR)
                PressureUnit.MILLIBAR -> this
            }
        }
    }

    override fun formattedValue(): String {
        return "${value.roundToInt()}"
    }

    override fun base(): QuantityUnit {
        return this.hpa
    }
}

enum class VelocityUnit(override val stringRes: StringResource) : MeasureUnit {
    METERS_PER_SECOND(SharedRes.strings.unit_meters_per_second),
    MILES_PER_HOUR(SharedRes.strings.unit_miles_per_hour);
}

data class Velocity(override val value: Float, override val unit: VelocityUnit) : QuantityUnit {

    companion object {

        val Velocity.mph get() = convertUnit(VelocityUnit.MILES_PER_HOUR)
        val Number.mph get() = Velocity(toFloat(), VelocityUnit.MILES_PER_HOUR)
        val Velocity.mps get() = convertUnit(VelocityUnit.METERS_PER_SECOND)
        val Number.mps get() = Velocity(toFloat(), VelocityUnit.METERS_PER_SECOND)

        private fun Velocity.convertUnit(targetUnit: VelocityUnit): Velocity {
            return when (targetUnit) {
                VelocityUnit.METERS_PER_SECOND -> toMetersPerSecond()
                VelocityUnit.MILES_PER_HOUR -> toMilesPerHour()
            }
        }

        private fun Velocity.toMetersPerSecond(): Velocity {
            return when (unit) {
                VelocityUnit.METERS_PER_SECOND -> this
                VelocityUnit.MILES_PER_HOUR -> Velocity((value * 0.447f).roundDecimals(3), VelocityUnit.METERS_PER_SECOND)
            }
        }

        private fun Velocity.toMilesPerHour(): Velocity {
            return when (unit) {
                VelocityUnit.METERS_PER_SECOND -> Velocity((value / 0.447f).roundDecimals(3), VelocityUnit.MILES_PER_HOUR)
                VelocityUnit.MILES_PER_HOUR -> this
            }
        }
    }

    override fun formattedValue(): String {
        return if (value == 0f) {
            "0"
        } else {
            "${value.roundDecimals(1)}"
        }
    }

    override fun base(): QuantityUnit {
        return this.mps
    }
}

enum class AngleUnit(override val stringRes: StringResource) : MeasureUnit {
    DEGREE(SharedRes.strings.unit_degree);
}

data class Angle(override val value: Float, override val unit: AngleUnit) : QuantityUnit {

    companion object {

        val Angle.deg get() = convertUnit(AngleUnit.DEGREE)
        val Number.deg get() = Angle(toFloat(), AngleUnit.DEGREE)

        private fun Angle.convertUnit(targetUnit: AngleUnit): Angle {
            return when (targetUnit) {
                AngleUnit.DEGREE -> toDegree()
            }
        }

        private fun Angle.toDegree(): Angle {
            return when (unit) {
                AngleUnit.DEGREE -> this
            }
        }
    }

    override fun formattedValue(): String {
        return "${value.roundToInt()}"
    }

    override fun base(): QuantityUnit {
        return this.deg
    }
}

interface QuantityUnit : Comparable<QuantityUnit> {

    val value: Float
    val unit: MeasureUnit

    fun base(): QuantityUnit

    fun formattedValue(): String

    override fun compareTo(other: QuantityUnit): Int {
        return this.base().value.compareTo(other.base().value)
    }
}

interface MeasureUnit {

    val stringRes: StringResource
}

@OptIn(ExperimentalStdlibApi::class)
fun temperatureRange(lower: Temperature, upper: Temperature): OpenEndRange<Temperature> {
    return lower.rangeUntil(upper)
}

@OptIn(ExperimentalStdlibApi::class)
fun velocityRange(lower: Velocity, upper: Velocity): OpenEndRange<Velocity> {
    return lower.rangeUntil(upper)
}