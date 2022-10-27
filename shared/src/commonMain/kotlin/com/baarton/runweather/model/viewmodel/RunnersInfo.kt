@file:OptIn(ExperimentalStdlibApi::class)

package com.baarton.runweather.model.viewmodel

import com.baarton.runweather.db.PersistedWeather
import com.baarton.runweather.model.TempUnit.*
import com.baarton.runweather.model.kelvinRange
import com.baarton.runweather.model.metersPerSecondRange
import com.baarton.runweather.model.weather.WeatherId
import com.baarton.runweather.res.SharedRes
import com.baarton.runweather.ui.Vector
import dev.icerock.moko.resources.StringResource
import kotlinx.datetime.Clock
import kotlin.reflect.KClass

/*
 * Velocity ranges are intended to be in meters per second
 * Temperature ranges are intended to be in Kelvin
 */
sealed class RunnersInfo {

    object WindWarning : WeatherWarning {

        private val RES_WARNING_STRONG = SharedRes.strings.weather_runners_info_alert_card_wind_strong
        private val RES_WARNING_MODERATE = SharedRes.strings.weather_runners_info_alert_card_wind_moderate

        private enum class WindWarningHint(override val textRes: StringResource, override val vector: Vector) : WarningHint {
            STRONG(RES_WARNING_STRONG, Vector.WARNING_RED),
            MODERATE(RES_WARNING_MODERATE, Vector.WARNING_YELLOW);
        }

        override fun warning(weatherData: PersistedWeather): WarningHint? {
            return when (weatherData.wind.speed.toFloat()) {
                in metersPerSecondRange(14.0f, Float.POSITIVE_INFINITY) -> WindWarningHint.STRONG
                in metersPerSecondRange(3.5f, 14.0f) -> WindWarningHint.MODERATE
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
            return when (weatherData.mainData.temperature.toFloat()) {
                in kelvinRange(28.0f, Float.POSITIVE_INFINITY) -> TemperatureWarningHint.HIGH_TEMP
                in kelvinRange(15.0f, 17.0f) -> TemperatureWarningHint.IDEAL
                in kelvinRange(Float.NEGATIVE_INFINITY, -5.0f) -> TemperatureWarningHint.LOW_TEMP
                else -> null
            }
        }
    }

    object LayersTop : TemperatureHint {

        private val RES_TOP_LAYER_ONE = SharedRes.strings.weather_runners_info_data_top_layers_one
        private val RES_TOP_LAYER_TWO = SharedRes.strings.weather_runners_info_data_top_layers_two
        private val RES_TOP_LAYER_THREE = SharedRes.strings.weather_runners_info_data_top_layers_three
        private val RES_TOP_LAYER_FOUR = SharedRes.strings.weather_runners_info_data_top_layers_four

        override fun slow(temp: Float): TextHint {
            return when (temp) {
                in kelvinRange(15.0f, Float.POSITIVE_INFINITY) -> LayersTopHint.ONE
                in kelvinRange(10.0f, 15.0f) -> LayersTopHint.TWO
                in kelvinRange(-1.0f, 10.0f) -> LayersTopHint.THREE
                in kelvinRange(Float.NEGATIVE_INFINITY, -1.0f) -> LayersTopHint.FOUR
                else -> throwException(temp, LayersTop::class)
            }
        }

        override fun fast(temp: Float): TextHint {
            return when (temp) {
                in kelvinRange(13.0f, Float.POSITIVE_INFINITY) -> LayersTopHint.ONE
                in kelvinRange(8.0f, 13.0f) -> LayersTopHint.TWO
                in kelvinRange(-3.0f, 8.0f) -> LayersTopHint.THREE
                in kelvinRange(Float.NEGATIVE_INFINITY, -3.0f) -> LayersTopHint.FOUR
                else -> throwException(temp, LayersTop::class)
            }
        }

        private enum class LayersTopHint(override val textRes: StringResource) : TextHint {
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

        override fun slow(temp: Float): TextHint {
            return when (temp) {
                in kelvinRange(13.5f, Float.POSITIVE_INFINITY) -> LayersBottomHint.SHORTS
                in kelvinRange(0.0f, 13.5f) -> LayersBottomHint.LONG_SLEEVED
                in kelvinRange(Float.NEGATIVE_INFINITY, 13.5f) -> LayersBottomHint.LONG_SLEEVED_DOUBLE
                else -> throwException(temp, LayersBottom::class)
            }
        }

        override fun fast(temp: Float): TextHint {
            return when (temp) {
                in kelvinRange(10.5f, Float.POSITIVE_INFINITY) -> LayersBottomHint.SHORTS
                in kelvinRange(-3.0f, 10.5f) -> LayersBottomHint.LONG_SLEEVED
                in kelvinRange(Float.NEGATIVE_INFINITY, -3.0f) -> LayersBottomHint.LONG_SLEEVED_DOUBLE
                else -> throwException(temp, LayersBottom::class)
            }
        }

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

        override fun slow(temp: Float): TextHint {
            return when (temp) {
                in kelvinRange(28.0f, Float.POSITIVE_INFINITY) -> HeadCoverHint.SUN
                in kelvinRange(14.5f, 28.0f) -> HeadCoverHint.NONE
                in kelvinRange(9.0f, 14.5f) -> HeadCoverHint.EARS
                in kelvinRange(Float.NEGATIVE_INFINITY, 9.0f) -> HeadCoverHint.CAP
                else -> throwException(temp, HeadCover::class)
            }
        }

        override fun fast(temp: Float): TextHint {
            return when (temp) {
                in kelvinRange(28.0f, Float.POSITIVE_INFINITY) -> HeadCoverHint.SUN
                in kelvinRange(12.5f, 28.0f) -> HeadCoverHint.NONE
                in kelvinRange(7.0f, 12.5f) -> HeadCoverHint.EARS
                in kelvinRange(Float.NEGATIVE_INFINITY, 7.0f) -> HeadCoverHint.CAP
                else -> throwException(temp, HeadCover::class)
            }
        }

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
            val now = Clock.System.now().epochSeconds
            val isDaylight = weatherData.sys.sunrise.toLong() < now && now < weatherData.sys.sunset.toLong()

            val weatherId = weatherData.weatherList.firstOrNull()?.weatherId ?: WeatherId.UNKNOWN

            return when {
                isDaylight && WeatherId.sunglassesList.contains(weatherId) -> SunglassesHint.YES
                !isDaylight && WeatherId.sunglassesList.contains(weatherId) -> SunglassesHint.NO
                !isDaylight && WeatherId.noSunglassesList.contains(weatherId) -> SunglassesHint.NO
                isDaylight && WeatherId.noSunglassesList.contains(weatherId) -> SunglassesHint.NO
                else -> SunglassesHint.UNKNOWN
            }
        }

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

        override fun slow(temp: Float): TextHint {
            return when (temp) {
                in kelvinRange(9.0f, Float.POSITIVE_INFINITY) -> NeckCoverHint.NONE
                in kelvinRange(5.5f, 9.0f) -> NeckCoverHint.WEAK
                in kelvinRange(Float.NEGATIVE_INFINITY, 5.5f) -> NeckCoverHint.STRONG
                else -> throwException(temp, NeckCover::class)
            }
        }

        override fun fast(temp: Float): TextHint {
            return when (temp) {
                in kelvinRange(7.5f, Float.POSITIVE_INFINITY) -> NeckCoverHint.NONE
                in kelvinRange(4.0f, 7.5f) -> NeckCoverHint.WEAK
                in kelvinRange(Float.NEGATIVE_INFINITY, 4.0f) -> NeckCoverHint.STRONG
                else -> throwException(temp, NeckCover::class)
            }
        }

        enum class NeckCoverHint(override val textRes: StringResource) : TextHint {
            NONE(RES_NECK_NONE),
            WEAK(RES_NECK_WEAK),
            STRONG(RES_NECK_STRONG);
        }
    }

    object Gloves : TemperatureHint {

        private val RES_GLOVES_NO = SharedRes.strings.weather_runners_info_data_gloves_no
        private val RES_GLOVES_YES = SharedRes.strings.weather_runners_info_data_gloves_yes

        override fun slow(temp: Float): TextHint {
            return when (temp) {
                in kelvinRange(4.5f, Float.POSITIVE_INFINITY) -> GlovesHint.NO
                in kelvinRange(Float.NEGATIVE_INFINITY, 4.5f) -> GlovesHint.YES
                else -> throwException(temp, Gloves::class)
            }
        }

        override fun fast(temp: Float): TextHint {
            return when (temp) {
                in kelvinRange(3.0f, Float.POSITIVE_INFINITY) -> GlovesHint.NO
                in kelvinRange(Float.NEGATIVE_INFINITY, 3.0f) -> GlovesHint.YES
                else -> throwException(temp, Gloves::class)
            }
        }

        enum class GlovesHint(override val textRes: StringResource) : TextHint {
            NO(RES_GLOVES_NO),
            YES(RES_GLOVES_YES);
        }
    }

    object Socks : TemperatureHint {

        private val RES_SOCKS_NORMAL = SharedRes.strings.weather_runners_info_data_socks_normal
        private val RES_SOCKS_WARM = SharedRes.strings.weather_runners_info_data_socks_warm

        override fun slow(temp: Float): TextHint {
            return when (temp) {
                in kelvinRange(-1.5f, Float.POSITIVE_INFINITY) -> SocksHint.NORMAL
                in kelvinRange(Float.NEGATIVE_INFINITY, -1.5f) -> SocksHint.WARM
                else -> throwException(temp, Socks::class)
            }
        }

        override fun fast(temp: Float): TextHint {
            return when (temp) {
                in kelvinRange(-3.0f, Float.POSITIVE_INFINITY) -> SocksHint.NORMAL
                in kelvinRange(Float.NEGATIVE_INFINITY, -3.0f) -> SocksHint.WARM
                else -> throwException(temp, Socks::class)
            }
        }

        enum class SocksHint(override val textRes: StringResource) : TextHint {
            NORMAL(RES_SOCKS_NORMAL),
            WARM(RES_SOCKS_WARM);
        }
    }
}

interface TemperatureHint : RunnersHint {

    fun slow(temp: Float): TextHint
    fun fast(temp: Float): TextHint

    fun throwException(temp: Float, cls: KClass<*>): TextHint {
        throw IllegalArgumentException("Illegal argument for the temperature range: $temp while attempting to resolve ${cls.simpleName}.")
    }
}

interface WeatherHint : RunnersHint {

    fun hint(weatherData: PersistedWeather): TextHint
}

sealed interface RunnersHint

interface WeatherWarning {
    fun warning(weatherData: PersistedWeather): WarningHint?
}

interface TextHint {
    val textRes: StringResource
}

interface WarningHint : TextHint {
    val vector: Vector
}