@file:OptIn(ExperimentalStdlibApi::class)

package com.baarton.runweather.model.viewmodel

import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.model.TempUnit.*
import com.baarton.runweather.model.Temperature
import com.baarton.runweather.model.Temperature.Companion.celsius
import com.baarton.runweather.model.Temperature.Companion.kelvin
import com.baarton.runweather.model.Velocity.Companion.mps
import com.baarton.runweather.model.temperatureRange
import com.baarton.runweather.model.velocityRange
import com.baarton.runweather.model.weather.WeatherId
import com.baarton.runweather.res.SharedRes
import com.baarton.runweather.ui.Vector
import dev.icerock.moko.resources.StringResource
import kotlinx.datetime.Clock
import kotlin.reflect.KClass


sealed class RunnersInfo {

    object WindWarning : WeatherWarning {

        private val RES_WARNING_STRONG = SharedRes.strings.weather_runners_info_alert_card_wind_strong
        private val RES_WARNING_MODERATE = SharedRes.strings.weather_runners_info_alert_card_wind_moderate

        private enum class WindWarningHint(override val textRes: StringResource, override val vector: Vector) : WarningHint {
            STRONG(RES_WARNING_STRONG, Vector.WARNING_RED),
            MODERATE(RES_WARNING_MODERATE, Vector.WARNING_YELLOW);
        }

        override fun warning(weatherData: PersistedWeather): WarningHint? {
            return when (weatherData.wind.velocity) {
                in velocityRange(14.0f.mps, Float.POSITIVE_INFINITY.mps) -> WindWarningHint.STRONG
                in velocityRange(3.5f.mps, 14.0f.mps) -> WindWarningHint.MODERATE
                else -> null
            }
        }
    }

    object TemperatureWarning : WeatherWarning {

        private val RES_WARNING_HIGH_TEMP = SharedRes.strings.weather_runners_info_alert_card_temp_high
        private val RES_WARNING_IDEAL = SharedRes.strings.weather_runners_info_alert_card_temp_ideal
        private val RES_WARNING_LOW_TEMP = SharedRes.strings.weather_runners_info_alert_card_temp_low

        private enum class TemperatureWarningHint(override val textRes: StringResource, override val vector: Vector) : WarningHint {
            HIGH_TEMP(RES_WARNING_HIGH_TEMP, Vector.WARNING_HOT),
            IDEAL(RES_WARNING_IDEAL, Vector.WARNING_OK),
            LOW_TEMP(RES_WARNING_LOW_TEMP, Vector.WARNING_COLD);
        }

        override fun warning(weatherData: PersistedWeather): WarningHint? {
            return when (weatherData.mainData.temperature) {
                in temperatureRange(28.0f.celsius, Float.POSITIVE_INFINITY.celsius) -> TemperatureWarningHint.HIGH_TEMP
                in temperatureRange(15.0f.celsius, 17.0f.celsius) -> TemperatureWarningHint.IDEAL
                in temperatureRange(0.kelvin, (-5.0f).celsius) -> TemperatureWarningHint.LOW_TEMP
                else -> null
            }
        }
    }

    object LayersTop : TemperatureHint {

        private val RES_TOP_LAYER_ONE = SharedRes.strings.weather_runners_info_data_top_layers_one
        private val RES_TOP_LAYER_TWO = SharedRes.strings.weather_runners_info_data_top_layers_two
        private val RES_TOP_LAYER_THREE = SharedRes.strings.weather_runners_info_data_top_layers_three
        private val RES_TOP_LAYER_FOUR = SharedRes.strings.weather_runners_info_data_top_layers_four

        override fun slow(temp: Temperature): TextHint {
            return when (temp) {
                in temperatureRange(15.0f.celsius, Float.POSITIVE_INFINITY.celsius) -> LayersTopHint.ONE
                in temperatureRange(10.0f.celsius, 15.0f.celsius) -> LayersTopHint.TWO
                in temperatureRange((-1.0f).celsius, 10.0f.celsius) -> LayersTopHint.THREE
                in temperatureRange(0.kelvin, (-1.0f).celsius) -> LayersTopHint.FOUR
                else -> throwException(temp, LayersTop::class)
            }
        }

        override fun fast(temp: Temperature): TextHint {
            return when (temp) {
                in temperatureRange(13.0f.celsius, Float.POSITIVE_INFINITY.celsius) -> LayersTopHint.ONE
                in temperatureRange(8.0f.celsius, 13.0f.celsius) -> LayersTopHint.TWO
                in temperatureRange((-3.0f).celsius, 8.0f.celsius) -> LayersTopHint.THREE
                in temperatureRange(0.kelvin, (-3.0f).celsius) -> LayersTopHint.FOUR
                else -> throwException(temp, LayersTop::class)
            }
        }

        override val titleRes: StringResource
            get() = SharedRes.strings.weather_runners_info_data_top_layers_category

        enum class LayersTopHint(override val textRes: StringResource) : TextHint {
            ONE(RES_TOP_LAYER_ONE),
            TWO(RES_TOP_LAYER_TWO),
            THREE(RES_TOP_LAYER_THREE),
            FOUR(RES_TOP_LAYER_FOUR);
        }
    }

    object LayersBottom : TemperatureHint {

        private val RES_BOTTOM_LAYER_SHORTS = SharedRes.strings.weather_runners_info_data_bottom_layers_shorts
        private val RES_BOTTOM_LAYER_LONG_SLEEVE = SharedRes.strings.weather_runners_info_data_bottom_layers_long_sleeved
        private val RES_BOTTOM_LAYER_LONG_SLEEVE_DOUBLE = SharedRes.strings.weather_runners_info_data_bottom_layers_long_sleeved_double

        override fun slow(temp: Temperature): TextHint {
            return when (temp) {
                in temperatureRange(13.5f.celsius, Float.POSITIVE_INFINITY.celsius) -> LayersBottomHint.SHORTS
                in temperatureRange(0.0f.celsius, 13.5f.celsius) -> LayersBottomHint.LONG_SLEEVED
                in temperatureRange(0.kelvin, 0.0f.celsius) -> LayersBottomHint.LONG_SLEEVED_DOUBLE
                else -> throwException(temp, LayersBottom::class)
            }
        }

        override fun fast(temp: Temperature): TextHint {
            return when (temp) {
                in temperatureRange(10.5f.celsius, Float.POSITIVE_INFINITY.celsius) -> LayersBottomHint.SHORTS
                in temperatureRange((-3.0f).celsius, 10.5f.celsius) -> LayersBottomHint.LONG_SLEEVED
                in temperatureRange(0.kelvin, (-3.0f).celsius) -> LayersBottomHint.LONG_SLEEVED_DOUBLE
                else -> throwException(temp, LayersBottom::class)
            }
        }

        override val titleRes: StringResource
            get() = SharedRes.strings.weather_runners_info_data_bottom_layers_category

        enum class LayersBottomHint(override val textRes: StringResource) : TextHint {
            SHORTS(RES_BOTTOM_LAYER_SHORTS),
            LONG_SLEEVED(RES_BOTTOM_LAYER_LONG_SLEEVE),
            LONG_SLEEVED_DOUBLE(RES_BOTTOM_LAYER_LONG_SLEEVE_DOUBLE);
        }
    }

    object HeadCover : TemperatureHint {

        private val RES_HEAD_SUN = SharedRes.strings.weather_runners_info_data_head_cover_sun
        private val RES_HEAD_NONE = SharedRes.strings.weather_runners_info_data_head_cover_none
        private val RES_HEAD_EARS = SharedRes.strings.weather_runners_info_data_head_cover_ears
        private val RES_HEAD_CAP = SharedRes.strings.weather_runners_info_data_head_cover_cap

        override fun slow(temp: Temperature): TextHint {
            return when (temp) {
                in temperatureRange(28.0f.celsius, Float.POSITIVE_INFINITY.celsius) -> HeadCoverHint.SUN
                in temperatureRange(14.5f.celsius, 28.0f.celsius) -> HeadCoverHint.NONE
                in temperatureRange(9.0f.celsius, 14.5f.celsius) -> HeadCoverHint.EARS
                in temperatureRange(0.kelvin, 9.0f.celsius) -> HeadCoverHint.CAP
                else -> throwException(temp, HeadCover::class)
            }
        }

        override fun fast(temp: Temperature): TextHint {
            return when (temp) {
                in temperatureRange(28.0f.celsius, Float.POSITIVE_INFINITY.celsius) -> HeadCoverHint.SUN
                in temperatureRange(12.5f.celsius, 28.0f.celsius) -> HeadCoverHint.NONE
                in temperatureRange(7.0f.celsius, 12.5f.celsius) -> HeadCoverHint.EARS
                in temperatureRange(0.kelvin, 7.0f.celsius) -> HeadCoverHint.CAP
                else -> throwException(temp, HeadCover::class)
            }
        }

        override val titleRes: StringResource
            get() = SharedRes.strings.weather_runners_info_data_head_cover_category

        enum class HeadCoverHint(override val textRes: StringResource) : TextHint {
            SUN(RES_HEAD_SUN),
            NONE(RES_HEAD_NONE),
            EARS(RES_HEAD_EARS),
            CAP(RES_HEAD_CAP);
        }
    }

    object Sunglasses : WeatherHint {

        private val RES_SUNGLASSES_NO = SharedRes.strings.weather_runners_info_data_sunglasses_no
        private val RES_SUNGLASSES_YES = SharedRes.strings.weather_runners_info_data_sunglasses_yes
        private val RES_SUNGLASSES_UNKNOWN = SharedRes.strings.app_n_a

        override fun hint(weatherData: PersistedWeather): TextHint {
            val now = Clock.System.now()
            val isDaylight = weatherData.sys.sunrise < now && now < weatherData.sys.sunset

            val weatherId = weatherData.weatherList.firstOrNull()?.weatherId ?: WeatherId.UNKNOWN

            return when {
                isDaylight && WeatherId.sunglassesList.contains(weatherId) -> SunglassesHint.YES
                !isDaylight && WeatherId.sunglassesList.contains(weatherId) -> SunglassesHint.NO
                !isDaylight && WeatherId.noSunglassesList.contains(weatherId) -> SunglassesHint.NO
                isDaylight && WeatherId.noSunglassesList.contains(weatherId) -> SunglassesHint.NO
                else -> SunglassesHint.UNKNOWN
            }
        }

        override val titleRes: StringResource
            get() = SharedRes.strings.weather_runners_info_data_sunglasses_category

        enum class SunglassesHint(override val textRes: StringResource) : TextHint {
            NO(RES_SUNGLASSES_NO),
            YES(RES_SUNGLASSES_YES),
            UNKNOWN(RES_SUNGLASSES_UNKNOWN);
        }
    }

    object NeckCover : TemperatureHint {

        private val RES_NECK_NONE = SharedRes.strings.weather_runners_info_data_neck_cover_none
        private val RES_NECK_WEAK = SharedRes.strings.weather_runners_info_data_neck_cover_weak
        private val RES_NECK_STRONG = SharedRes.strings.weather_runners_info_data_neck_cover_strong

        override fun slow(temp: Temperature): TextHint {
            return when (temp) {
                in temperatureRange(9.0f.celsius, Float.POSITIVE_INFINITY.celsius) -> NeckCoverHint.NONE
                in temperatureRange(5.5f.celsius, 9.0f.celsius) -> NeckCoverHint.WEAK
                in temperatureRange(0.kelvin, 5.5f.celsius) -> NeckCoverHint.STRONG
                else -> throwException(temp, NeckCover::class)
            }
        }

        override fun fast(temp: Temperature): TextHint {
            return when (temp) {
                in temperatureRange(7.5f.celsius, Float.POSITIVE_INFINITY.celsius) -> NeckCoverHint.NONE
                in temperatureRange(4.0f.celsius, 7.5f.celsius) -> NeckCoverHint.WEAK
                in temperatureRange(0.kelvin, 4.0f.celsius) -> NeckCoverHint.STRONG
                else -> throwException(temp, NeckCover::class)
            }
        }

        override val titleRes: StringResource
            get() = SharedRes.strings.weather_runners_info_data_neck_cover_category

        enum class NeckCoverHint(override val textRes: StringResource) : TextHint {
            NONE(RES_NECK_NONE),
            WEAK(RES_NECK_WEAK),
            STRONG(RES_NECK_STRONG);
        }
    }

    object Gloves : TemperatureHint {

        private val RES_GLOVES_NO = SharedRes.strings.weather_runners_info_data_gloves_no
        private val RES_GLOVES_YES = SharedRes.strings.weather_runners_info_data_gloves_yes

        override fun slow(temp: Temperature): TextHint {
            return when (temp) {
                in temperatureRange(4.5f.celsius, Float.POSITIVE_INFINITY.celsius) -> GlovesHint.NO
                in temperatureRange(0.kelvin, 4.5f.celsius) -> GlovesHint.YES
                else -> throwException(temp, Gloves::class)
            }
        }

        override fun fast(temp: Temperature): TextHint {
            return when (temp) {
                in temperatureRange(3.0f.celsius, Float.POSITIVE_INFINITY.celsius) -> GlovesHint.NO
                in temperatureRange(0.kelvin, 3.0f.celsius) -> GlovesHint.YES
                else -> throwException(temp, Gloves::class)
            }
        }

        override val titleRes: StringResource
            get() = SharedRes.strings.weather_runners_info_data_gloves_category

        enum class GlovesHint(override val textRes: StringResource) : TextHint {
            NO(RES_GLOVES_NO),
            YES(RES_GLOVES_YES);
        }
    }

    object Socks : TemperatureHint {

        private val RES_SOCKS_NORMAL = SharedRes.strings.weather_runners_info_data_socks_normal
        private val RES_SOCKS_WARM = SharedRes.strings.weather_runners_info_data_socks_warm

        override fun slow(temp: Temperature): TextHint {
            return when (temp) {
                in temperatureRange((-1.5f).celsius, Float.POSITIVE_INFINITY.celsius) -> SocksHint.NORMAL
                in temperatureRange(0.kelvin, (-1.5f).celsius) -> SocksHint.WARM
                else -> throwException(temp, Socks::class)
            }
        }

        override fun fast(temp: Temperature): TextHint {
            return when (temp) {
                in temperatureRange((-3.0f).celsius, Float.POSITIVE_INFINITY.celsius) -> SocksHint.NORMAL
                in temperatureRange(0.kelvin, (-3.0f).celsius) -> SocksHint.WARM
                else -> throwException(temp, Socks::class)
            }
        }

        override val titleRes: StringResource
            get() = SharedRes.strings.weather_runners_info_data_socks_category

        enum class SocksHint(override val textRes: StringResource) : TextHint {
            NORMAL(RES_SOCKS_NORMAL),
            WARM(RES_SOCKS_WARM);
        }
    }
}

interface TemperatureHint : RunnersHint {

    fun slow(temp: Temperature): TextHint
    fun fast(temp: Temperature): TextHint

    fun throwException(temp: Temperature, cls: KClass<*>): TextHint {
        throw IllegalArgumentException("Illegal argument for the temperature range: $temp while attempting to resolve ${cls.simpleName}.")
    }
}

interface WeatherHint : RunnersHint {

    fun hint(weatherData: PersistedWeather): TextHint
}

sealed interface RunnersHint {
    val titleRes: StringResource
}

interface WeatherWarning {
    fun warning(weatherData: PersistedWeather): WarningHint?
}

interface TextHint {
    val textRes: StringResource
}

interface WarningHint : TextHint {
    val vector: Vector
}