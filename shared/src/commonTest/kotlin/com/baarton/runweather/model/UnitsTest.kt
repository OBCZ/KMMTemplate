package com.baarton.runweather.model

import com.baarton.runweather.model.Angle.Companion.deg
import com.baarton.runweather.model.Height.Companion.inch
import com.baarton.runweather.model.Height.Companion.mm
import com.baarton.runweather.model.Humidity.Companion.percent
import com.baarton.runweather.model.Pressure.Companion.hpa
import com.baarton.runweather.model.Pressure.Companion.mbar
import com.baarton.runweather.model.Temperature.Companion.celsius
import com.baarton.runweather.model.Temperature.Companion.fahrenheit
import com.baarton.runweather.model.Temperature.Companion.kelvin
import com.baarton.runweather.model.Velocity.Companion.mph
import com.baarton.runweather.model.Velocity.Companion.mps
import kotlin.test.Test
import kotlin.test.assertEquals


class UnitsFormattingTest {

    @Test
    fun tempFormattingTest() {
        val temperature = 274.313f.kelvin
        assertEquals("274.3", temperature.kelvin.formattedValue())
        assertEquals("1.2", temperature.celsius.formattedValue())
        assertEquals("34.1", temperature.fahrenheit.formattedValue())
    }

    @Test
    fun humidityFormattingTest() {
        val humidity = 74.5f.percent
        assertEquals("75", humidity.percent.formattedValue())
    }

    @Test
    fun pressureFormattingTest() {
        val pressure = 1025.313f.hpa
        assertEquals("1025", pressure.hpa.formattedValue())
        assertEquals("1025", pressure.mbar.formattedValue())
    }

    @Test
    fun rainfallFormattingTest() {
        val height = 12.313f.mm
        assertEquals("12.3", height.mm.formattedValue())
        assertEquals("0.5", height.inch.formattedValue())
    }

    @Test
    fun windSpeedFormattingTest() {
        val velocity = 6.313f.mps
        assertEquals("6.3", velocity.mps.formattedValue())
        assertEquals("14.1", velocity.mph.formattedValue())
    }

    @Test
    fun windAngleFormattingTest() {
        val angle = 214.5f.deg
        assertEquals("215", angle.deg.formattedValue())
    }
}

class TempUnitTest {

    data class TempUnitTestParam(
        override val inputUnit: Temperature,
        val expectedKelvin: Temperature,
        val expectedCelsius: Temperature,
        val expectedFahrenheit: Temperature
    ) : TestParam

    private val tempTestParams by lazy {
        listOf(
            TempUnitTestParam((-30f).fahrenheit, 238.706f.kelvin, (-34.444f).celsius, (-30f).fahrenheit),
            TempUnitTestParam((-10f).fahrenheit, 249.817f.kelvin, (-23.333f).celsius, (-10f).fahrenheit),
            TempUnitTestParam(0f.fahrenheit, 255.372f.kelvin, (-17.778f).celsius, 0f.fahrenheit),
            TempUnitTestParam(10.0f.fahrenheit, 260.928f.kelvin, (-12.222f).celsius, 10.0f.fahrenheit),
            TempUnitTestParam(30.5f.fahrenheit, 272.317f.kelvin, (-0.833f).celsius, 30.50f.fahrenheit),
            TempUnitTestParam(50.6f.fahrenheit, 283.483f.kelvin, 10.333f.celsius, 50.6f.fahrenheit),
            TempUnitTestParam(70.7f.fahrenheit, 294.65f.kelvin, 21.5f.celsius, 70.7f.fahrenheit),
            TempUnitTestParam(92.3f.fahrenheit, 306.65f.kelvin, 33.5f.celsius, 92.30f.fahrenheit),
            TempUnitTestParam(111.6f.fahrenheit, 317.372f.kelvin, 44.222f.celsius, 111.6f.fahrenheit),
            TempUnitTestParam(0f.kelvin, 0.0f.kelvin, (-273.15f).celsius, (-459.67f).fahrenheit),
            TempUnitTestParam(263.15f.kelvin, 263.15f.kelvin, (-10.0f).celsius, 14.0f.fahrenheit),
            TempUnitTestParam(273.15f.kelvin, 273.150f.kelvin, 0f.celsius, 32.0f.fahrenheit),
            TempUnitTestParam(283.15f.kelvin, 283.15f.kelvin, 10f.celsius, 50.0f.fahrenheit),
            TempUnitTestParam(293.15f.kelvin, 293.15f.kelvin, 20f.celsius, 68.0f.fahrenheit),
            TempUnitTestParam(303.15f.kelvin, 303.15f.kelvin, 30.0f.celsius, 86.0f.fahrenheit),
            TempUnitTestParam(313.15f.kelvin, 313.150f.kelvin, 40f.celsius, 104.0f.fahrenheit),
            TempUnitTestParam((-20.0f).celsius, 253.150f.kelvin, (-20.0f).celsius, (-4.0f).fahrenheit),
            TempUnitTestParam((-10.0f).celsius, 263.150f.kelvin, (-10.0f).celsius, 14.0f.fahrenheit),
            TempUnitTestParam(0f.celsius, 273.150f.kelvin, 0f.celsius, 32.0f.fahrenheit),
            TempUnitTestParam(20.5f.celsius, 293.650f.kelvin, 20.5f.celsius, 68.9f.fahrenheit),
            TempUnitTestParam(30.5f.celsius, 303.650f.kelvin, 30.5f.celsius, 86.9f.fahrenheit),
            TempUnitTestParam(40.0f.celsius, 313.150f.kelvin, 40f.celsius, 104.0f.fahrenheit),
            TempUnitTestParam(50f.celsius, 323.150f.kelvin, 50f.celsius, 122.0f.fahrenheit),
        )
    }

    @Test
    fun toKelvinTest() {
        tempTestParams.testConvert(
            expectedFun = { it.expectedKelvin }
        ) { unitTestParam -> unitTestParam.inputUnit.kelvin }
    }

    @Test
    fun toCelsiusTest() {
        tempTestParams.testConvert(
            expectedFun = { it.expectedCelsius }
        ) { unitTestParam -> unitTestParam.inputUnit.celsius }
    }

    @Test
    fun toFahrenheitTest() {
        tempTestParams.testConvert(
            expectedFun = { it.expectedFahrenheit }
        ) { unitTestParam -> unitTestParam.inputUnit.fahrenheit }
    }
}

class RainfallUnitTest {

    data class RainUnitTestParam(
        override val inputUnit: Height,
        val expectedMillimeter: Height,
        val expectedInch: Height
    ) : TestParam

    private val rainTestParams by lazy {
        listOf(
            RainUnitTestParam(0f.mm, 0.0f.mm, 0f.inch),
            RainUnitTestParam(2f.mm, 2f.mm, 0.079f.inch),
            RainUnitTestParam(4f.mm, 4f.mm, 0.157f.inch),
            RainUnitTestParam(8f.mm, 8f.mm, 0.315f.inch),
            RainUnitTestParam(0.5f.inch, 12.7f.mm, 0.5f.inch),
            RainUnitTestParam(1.5f.inch, 38.1f.mm, 1.5f.inch),
            RainUnitTestParam(3f.inch, 76.2f.mm, 3.0f.inch),
            RainUnitTestParam(6f.inch, 152.4f.mm, 6.00f.inch)
        )
    }

    @Test
    fun toMillimeterTest() {
        rainTestParams.testConvert(
            expectedFun = { it.expectedMillimeter },
            actualFun = { unitTestParam -> unitTestParam.inputUnit.mm }
        )
    }

    @Test
    fun toInchTest() {
        rainTestParams.testConvert(
            expectedFun = { it.expectedInch },
            actualFun = { unitTestParam -> unitTestParam.inputUnit.inch }
        )
    }
}

class PressureUnitTest {

    data class PressureUnitTestParam(
        override val inputUnit: Pressure,
        val expectedHectoPascal: Pressure,
        val expectedMillibar: Pressure
    ) : TestParam

    private val pressureTestParams by lazy {
        listOf(
            PressureUnitTestParam(1000f.hpa, 1000.0f.hpa, 1000f.mbar),
            PressureUnitTestParam(1002f.hpa, 1002f.hpa, 1002.000f.mbar),
            PressureUnitTestParam(1004f.mbar, 1004.0f.hpa, 1004f.mbar),
            PressureUnitTestParam(1008f.mbar, 1008f.hpa, 1008f.mbar)
        )
    }

    @Test
    fun toHectoPascalTest() {
        pressureTestParams.testConvert(
            expectedFun = { it.expectedHectoPascal },
            actualFun = { unitTestParam -> unitTestParam.inputUnit.hpa }
        )
    }

    @Test
    fun toMillibarTest() {
        pressureTestParams.testConvert(
            expectedFun = { it.expectedMillibar },
            actualFun = { unitTestParam -> unitTestParam.inputUnit.mbar }
        )
    }
}

class WindSpeedUnitTest {

    data class WindSpeedUnitTestParam(
        override val inputUnit: Velocity,
        val expectedMilesPerHour: Velocity,
        val expectedMetersPerSecond: Velocity
    ) : TestParam

    private val windSpeedTestParams by lazy {
        listOf(
            WindSpeedUnitTestParam(3.5f.mps, 7.830f.mph, 3.5f.mps),
            WindSpeedUnitTestParam(0f.mps, 0f.mph, 0.0f.mps),
            WindSpeedUnitTestParam(10f.mps, 22.371f.mph, 10f.mps),
            WindSpeedUnitTestParam(15f.mps, 33.557f.mph, 15f.mps),
            WindSpeedUnitTestParam(3f.mph, 3f.mph, 1.341f.mps),
            WindSpeedUnitTestParam(10f.mph, 10.0f.mph, 4.470f.mps),
            WindSpeedUnitTestParam(25f.mph, 25f.mph, 11.175f.mps),
            WindSpeedUnitTestParam(35f.mph, 35f.mph, 15.645f.mps)
        )
    }

    @Test
    fun toMilesPerHourTest() {
        windSpeedTestParams.testConvert(
            expectedFun = { it.expectedMilesPerHour },
            actualFun = { unitTestParam -> unitTestParam.inputUnit.mph }
        )
    }

    @Test
    fun toMetersPerSecondTest() {
        windSpeedTestParams.testConvert(
            expectedFun = { it.expectedMetersPerSecond },
            actualFun = { unitTestParam -> unitTestParam.inputUnit.mps }
        )
    }
}

interface TestParam {
    val inputUnit: QuantityUnit
}

private fun <T : TestParam> List<T>.testConvert(expectedFun: (T) -> QuantityUnit, actualFun: (T) -> QuantityUnit) {
    this.forEach {
        val expected = expectedFun(it)
        val actual = actualFun(it)
        assertEquals(
            expected.value,
            actual.value,
            "The value was not converted as expected. Actual result: >>> ${actual.value} <<<; Expected result: >>> ${expected.value} <<<."
        )
        assertEquals(
            expected.unit,
            actual.unit,
            "The unit was not converted as expected. Actual result: >>> ${actual.unit} <<<; Expected result: >>> ${expected.unit} <<<."
        )
    }
}