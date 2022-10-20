package com.baarton.runweather.model

import com.baarton.runweather.model.HumidityUnit.PERCENT
import com.baarton.runweather.model.PressureUnit.HECTOPASCAL
import com.baarton.runweather.model.PressureUnit.MILLIBAR
import com.baarton.runweather.model.RainfallUnit.INCH
import com.baarton.runweather.model.RainfallUnit.MILLIMETER
import com.baarton.runweather.model.TempUnit.CELSIUS
import com.baarton.runweather.model.TempUnit.FAHRENHEIT
import com.baarton.runweather.model.TempUnit.KELVIN
import com.baarton.runweather.model.WindSpeedUnit.METERS_PER_SECOND
import com.baarton.runweather.model.WindSpeedUnit.MILES_PER_HOUR
import kotlin.test.Test
import kotlin.test.assertEquals


class UnitsFormattingTest {

    @Test
    fun tempFormattingTest() {
        val kelvinValue = 274.313f
        assertEquals("274.3", KELVIN.prepareValue(kelvinValue))
        assertEquals("1.2", CELSIUS.prepareValue(kelvinValue))
        assertEquals("34.1", FAHRENHEIT.prepareValue(kelvinValue))
    }

    @Test
    fun humidityFormattingTest() {
        val rawValue = 74.5f
        assertEquals("74", PERCENT.prepareValue(rawValue))
    }

    @Test
    fun pressureFormattingTest() {
        val hectoPascalValue = 1025.313f
        assertEquals("1025", HECTOPASCAL.prepareValue(hectoPascalValue))
        assertEquals("1025", MILLIBAR.prepareValue(hectoPascalValue))
    }

    @Test
    fun rainfallFormattingTest() {
        val millimeterValue = 12.313f
        assertEquals("12.3", MILLIMETER.prepareValue(millimeterValue))
        assertEquals("0.5", INCH.prepareValue(millimeterValue))
    }

    @Test
    fun windSpeedFormattingTest() {
        val meterPerSecondValue = 6.313f
        assertEquals("6.3", METERS_PER_SECOND.prepareValue(meterPerSecondValue))
        assertEquals("14.1", MILES_PER_HOUR.prepareValue(meterPerSecondValue))
    }
}

class TempUnitTest {

    data class TempUnitTestParam(
        override val inputValue: Float,
        val inputUnit: TempUnit,
        val expectedKelvin: Float,
        val expectedCelsius: Float,
        val expectedFahrenheit: Float
    ) : TestParam

    private val tempTestParams by lazy {
        listOf(
            TempUnitTestParam(-30f, FAHRENHEIT, 238.706f, -34.444f, -30f),
            TempUnitTestParam(-10f, FAHRENHEIT, 249.817f, -23.333f, -10f),
            TempUnitTestParam(0f, FAHRENHEIT, 255.372f, -17.778f, 0f),
            TempUnitTestParam(10.0f, FAHRENHEIT, 260.928f, -12.222f, 10.0f),
            TempUnitTestParam(30.5f, FAHRENHEIT, 272.317f, -0.833f, 30.50f),
            TempUnitTestParam(50.6f, FAHRENHEIT, 283.483f, 10.333f, 50.6f),
            TempUnitTestParam(70.7f, FAHRENHEIT, 294.65f, 21.5f, 70.7f),
            TempUnitTestParam(92.3f, FAHRENHEIT, 306.65f, 33.5f, 92.30f),
            TempUnitTestParam(111.6f, FAHRENHEIT, 317.372f, 44.222f, 111.6f),
            TempUnitTestParam(0f, KELVIN, 0.0f, -273.15f, -459.67f),
            TempUnitTestParam(263.15f, KELVIN, 263.15f, -10.0f, 14.0f),
            TempUnitTestParam(273.15f, KELVIN, 273.150f, 0f, 32.0f),
            TempUnitTestParam(283.15f, KELVIN, 283.15f, 10f, 50.0f),
            TempUnitTestParam(293.15f, KELVIN, 293.15f, 20f, 68.0f),
            TempUnitTestParam(303.15f, KELVIN, 303.15f, 30.0f, 86.0f),
            TempUnitTestParam(313.15f, KELVIN, 313.150f, 40f, 104.0f),
            TempUnitTestParam(-20.0f, CELSIUS, 253.150f, -20.0f, -4.0f),
            TempUnitTestParam(-10.0f, CELSIUS, 263.150f, -10.0f, 14.0f),
            TempUnitTestParam(0f, CELSIUS, 273.150f, 0f, 32.0f),
            TempUnitTestParam(20.5f, CELSIUS, 293.650f, 20.5f, 68.9f),
            TempUnitTestParam(30.5f, CELSIUS, 303.650f, 30.5f, 86.9f),
            TempUnitTestParam(40.0f, CELSIUS, 313.150f, 40f, 104.0f),
            TempUnitTestParam(50f, CELSIUS, 323.150f, 50f, 122.0f),
        )
    }

    @Test
    fun toKelvinTest() {
        tempTestParams.testConvert(
            expectedFun = { it.expectedKelvin },
            actualFun = { unitTestParam, inputValue -> unitTestParam.inputUnit.toKelvin(inputValue) }
        )
    }

    @Test
    fun toCelsiusTest() {
        tempTestParams.testConvert(
            expectedFun = { it.expectedCelsius },
            actualFun = { unitTestParam, inputValue -> unitTestParam.inputUnit.toCelsius(inputValue) }
        )
    }

    @Test
    fun toFahrenheitTest() {
        tempTestParams.testConvert(
            expectedFun = { it.expectedFahrenheit },
            actualFun = { unitTestParam, inputValue -> unitTestParam.inputUnit.toFahrenheit(inputValue) }
        )
    }
}

class RainfallUnitTest {

    data class RainUnitTestParam(
        override val inputValue: Float,
        val inputUnit: RainfallUnit,
        val expectedMillimeter: Float,
        val expectedInch: Float
    ) : TestParam

    private val rainTestParams by lazy {
        listOf(
            RainUnitTestParam(0f, MILLIMETER, 0.0f, 0f),
            RainUnitTestParam(2f, MILLIMETER, 2f, 0.079f),
            RainUnitTestParam(4f, MILLIMETER, 4f, 0.157f),
            RainUnitTestParam(8f, MILLIMETER, 8f, 0.315f),
            RainUnitTestParam(0.5f, INCH, 12.7f, 0.5f),
            RainUnitTestParam(1.5f, INCH, 38.1f, 1.5f),
            RainUnitTestParam(3f, INCH, 76.2f, 3.0f),
            RainUnitTestParam(6f, INCH, 152.4f, 6.00f)
        )
    }

    @Test
    fun toMillimeterTest() {
        rainTestParams.testConvert(
            expectedFun = { it.expectedMillimeter },
            actualFun = { unitTestParam, inputValue -> unitTestParam.inputUnit.toMillimeter(inputValue) }
        )
    }

    @Test
    fun toInchTest() {
        rainTestParams.testConvert(
            expectedFun = { it.expectedInch },
            actualFun = { unitTestParam, inputValue -> unitTestParam.inputUnit.toInch(inputValue) }
        )
    }
}

class PressureUnitTest {

    data class PressureUnitTestParam(
        override val inputValue: Float,
        val inputUnit: PressureUnit,
        val expectedHectoPascal: Float,
        val expectedMillibar: Float
    ) : TestParam

    private val pressureTestParams by lazy {
        listOf(
            PressureUnitTestParam(1000f, HECTOPASCAL, 1000.0f, 1000f),
            PressureUnitTestParam(1002f, HECTOPASCAL, 1002f, 1002.000f),
            PressureUnitTestParam(1004f, MILLIBAR, 1004.0f, 1004f),
            PressureUnitTestParam(1008f, MILLIBAR, 1008f, 1008f)
        )
    }

    @Test
    fun toHectoPascalTest() {
        pressureTestParams.testConvert(
            expectedFun = { it.expectedHectoPascal },
            actualFun = { unitTestParam, inputValue -> unitTestParam.inputUnit.toHectoPascal(inputValue) }
        )
    }

    @Test
    fun toMillibarTest() {
        pressureTestParams.testConvert(
            expectedFun = { it.expectedMillibar },
            actualFun = { unitTestParam, inputValue -> unitTestParam.inputUnit.toMillibar(inputValue) }
        )
    }
}

class WindSpeedUnitTest {

    data class WindSpeedUnitTestParam(
        override val inputValue: Float,
        val inputUnit: WindSpeedUnit,
        val expectedMilesPerHour: Float,
        val expectedMetersPerSecond: Float
    ) : TestParam

    private val windSpeedTestParams by lazy {
        listOf(
            WindSpeedUnitTestParam(3.5f, METERS_PER_SECOND, 7.830f, 3.5f),
            WindSpeedUnitTestParam(0f, METERS_PER_SECOND, 0f, 0.0f),
            WindSpeedUnitTestParam(10f, METERS_PER_SECOND, 22.371f, 10f),
            WindSpeedUnitTestParam(15f, METERS_PER_SECOND, 33.557f, 15f),
            WindSpeedUnitTestParam(3f, MILES_PER_HOUR, 3f, 1.341f),
            WindSpeedUnitTestParam(10f, MILES_PER_HOUR, 10.0f, 4.470f),
            WindSpeedUnitTestParam(25f, MILES_PER_HOUR, 25f, 11.175f),
            WindSpeedUnitTestParam(35f, MILES_PER_HOUR, 35f, 15.645f)
        )
    }

    @Test
    fun toMilesPerHourTest() {
        windSpeedTestParams.testConvert(
            expectedFun = { it.expectedMilesPerHour },
            actualFun = { unitTestParam, inputValue -> unitTestParam.inputUnit.toMilesPerHour(inputValue) }
        )
    }

    @Test
    fun toMetersPerSecondTest() {
        windSpeedTestParams.testConvert(
            expectedFun = { it.expectedMetersPerSecond },
            actualFun = { unitTestParam, inputValue -> unitTestParam.inputUnit.toMetersPerSecond(inputValue) }
        )
    }
}

interface TestParam {
    val inputValue: Float
}

private fun <T : TestParam> List<T>.testConvert(expectedFun: (T) -> Float, actualFun: (T, Float) -> Float) {
    this.forEach {
        val expected = expectedFun(it)
        val actual = actualFun(it, it.inputValue)
        assertEquals(
            expected,
            actual,
            "The unit was not converted as expected. Actual result: >>> $actual <<<; Expected result: >>> $expected <<<."
        )
    }
}