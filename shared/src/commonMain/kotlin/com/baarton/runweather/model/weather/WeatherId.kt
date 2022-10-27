package com.baarton.runweather.model.weather

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/*
 * See https://openweathermap.org/weather-conditions.
 */
enum class WeatherId(val id: String) {

    // Group 2xx: Thunderstorm
    THUNDERSTORM_WITH_LIGHT_RAIN("200"),
    THUNDERSTORM_WITH_RAIN("201"),
    THUNDERSTORM_WITH_HEAVY_RAIN("202"),
    LIGHT_THUNDERSTORM("210"),
    THUNDERSTORM("211"),
    HEAVY_THUNDERSTORM("212"),
    RAGGED_THUNDERSTORM("221"),
    THUNDERSTORM_WITH_LIGHT_DRIZZLE("230"),
    THUNDERSTORM_WITH_DRIZZLE("231"),
    THUNDERSTORM_WITH_HEAVY_DRIZZLE("232"),

    // Group 3xx: Drizzle
    LIGHT_INTENSITY_DRIZZLE("300"),
    DRIZZLE("301"),
    HEAVY_INTENSITY_DRIZZLE("302"),
    LIGHT_INTENSITY_DRIZZLE_RAIN("310"),
    DRIZZLE_RAIN("311"),
    HEAVY_INTENSITY_DRIZZLE_RAIN("312"),
    SHOWER_RAIN_AND_DRIZZLE("313"),
    HEAVY_SHOWER_RAIN_AND_DRIZZLE("314"),
    SHOWER_DRIZZLE("321"),

    // Group 5xx: Rain
    LIGHT_RAIN("500"),
    MODERATE_RAIN("501"),
    HEAVY_INTENSITY_RAIN("502"),
    VERY_HEAVY_RAIN("503"),
    EXTREME_RAIN("504"),
    FREEZING_RAIN("511"),
    LIGHT_INTENSITY_SHOWER_RAIN("520"),
    SHOWER_RAIN("521"),
    HEAVY_INTENSITY_SHOWER_RAIN("522"),
    RAGGED_SHOWER_RAIN("531"),

    // Group 6xx: Snow
    LIGHT_SNOW("600"),
    SNOW("601"),
    HEAVY_SNOW("602"),
    SLEET("611"),
    LIGHT_SHOWER_SLEET("612"),
    SHOWER_SLEET("613"),
    LIGHT_RAIN_AND_SNOW("615"),
    RAIN_AND_SNOW("616"),
    LIGHT_SHOWER_SNOW("620"),
    SHOWER_SNOW("621"),
    HEAVY_SHOWER_SNOW("622"),

    // Group 7xx: Atmosphere
    MIST("701"),
    SMOKE("711"),
    HAZE("721"),
    SAND_DUST_WHIRLS("731"),
    FOG("741"),
    SAND("751"),
    DUST("761"),
    VOLCANIC_ASH("762"),
    SQUALLS("771"),
    TORNADO("781"),

    // Group 800: Clear
    CLEAR_SKY("800"),

    // Group 80x: Clouds
    FEW_CLOUDS("801"), // 11-24%
    SCATTERED_CLOUDS("802"), // 25-50%
    BROKEN_CLOUDS("803"), // 51-84%
    OVERCAST_CLOUDS("804"), // 85-100%

    UNKNOWN("");

    companion object {

        private val thunderStormList = listOf(
            THUNDERSTORM_WITH_LIGHT_RAIN,
            THUNDERSTORM_WITH_RAIN,
            THUNDERSTORM_WITH_HEAVY_RAIN,
            LIGHT_THUNDERSTORM,
            THUNDERSTORM,
            HEAVY_THUNDERSTORM,
            RAGGED_THUNDERSTORM,
            THUNDERSTORM_WITH_LIGHT_DRIZZLE,
            THUNDERSTORM_WITH_DRIZZLE,
            THUNDERSTORM_WITH_HEAVY_DRIZZLE
        )
        private val drizzleList = listOf(
            LIGHT_INTENSITY_DRIZZLE,
            DRIZZLE,
            HEAVY_INTENSITY_DRIZZLE,
            LIGHT_INTENSITY_DRIZZLE_RAIN,
            DRIZZLE_RAIN,
            HEAVY_INTENSITY_DRIZZLE_RAIN,
            SHOWER_RAIN_AND_DRIZZLE,
            HEAVY_SHOWER_RAIN_AND_DRIZZLE,
            SHOWER_DRIZZLE
        )
        private val rainList = listOf(
            LIGHT_RAIN,
            MODERATE_RAIN,
            HEAVY_INTENSITY_RAIN,
            VERY_HEAVY_RAIN,
            EXTREME_RAIN,
            FREEZING_RAIN,
            LIGHT_INTENSITY_SHOWER_RAIN,
            SHOWER_RAIN,
            HEAVY_INTENSITY_SHOWER_RAIN,
            RAGGED_SHOWER_RAIN
        )
        private val snowList = listOf(
            LIGHT_SNOW,
            SNOW,
            HEAVY_SNOW,
            SLEET,
            LIGHT_SHOWER_SLEET,
            SHOWER_SLEET,
            LIGHT_RAIN_AND_SNOW,
            RAIN_AND_SNOW,
            LIGHT_SHOWER_SNOW,
            SHOWER_SNOW,
            HEAVY_SHOWER_SNOW
        )
        private val atmosphereList = listOf(MIST, SMOKE, HAZE, SAND_DUST_WHIRLS, FOG, SAND, DUST, VOLCANIC_ASH, SQUALLS, TORNADO)

        val sunglassesList = listOf(CLEAR_SKY, FEW_CLOUDS, SCATTERED_CLOUDS)
        val noSunglassesList = listOf(
            listOf(BROKEN_CLOUDS, OVERCAST_CLOUDS),
            thunderStormList,
            drizzleList,
            rainList,
            snowList,
            atmosphereList
        ).flatten()

        fun safeValueOf(id: String): WeatherId {
            return try {
                values().first { id == it.id }
            } catch (e: NoSuchElementException) {
                UNKNOWN
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = WeatherId::class)
object WeatherIdSerializer : KSerializer<WeatherId> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("WeatherId", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: WeatherId) {
        encoder.encodeString("$value")
    }

    override fun deserialize(decoder: Decoder): WeatherId {
        return WeatherId.safeValueOf(decoder.decodeString())
    }
}