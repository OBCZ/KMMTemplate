@file:OptIn(ExperimentalSerializationApi::class)

package com.baarton.runweather.ktor

import com.baarton.runweather.model.Angle
import com.baarton.runweather.model.Angle.Companion.deg
import com.baarton.runweather.model.Height
import com.baarton.runweather.model.Height.Companion.mm
import com.baarton.runweather.model.Humidity
import com.baarton.runweather.model.Humidity.Companion.percent
import com.baarton.runweather.model.Pressure
import com.baarton.runweather.model.Pressure.Companion.hpa
import com.baarton.runweather.model.Temperature
import com.baarton.runweather.model.Temperature.Companion.kelvin
import com.baarton.runweather.model.Velocity
import com.baarton.runweather.model.Velocity.Companion.mps
import com.baarton.runweather.model.weather.WeatherId
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


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

@Serializer(forClass = Instant::class)
object SecondsInstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString("$value")
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochSeconds(decoder.decodeString().toLong())
    }
}

@Serializer(forClass = Velocity::class)
object VelocitySerializer : KSerializer<Velocity> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Velocity", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Velocity) {
        encoder.encodeString("$value")
    }

    override fun deserialize(decoder: Decoder): Velocity {
        return decoder.decodeString().toFloat().mps
    }
}

@Serializer(forClass = Height::class)
object HeightSerializer : KSerializer<Height> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Height", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Height) {
        encoder.encodeString("$value")
    }

    override fun deserialize(decoder: Decoder): Height {
        return decoder.decodeString().toFloat().mm
    }
}

@Serializer(forClass = Angle::class)
object AngleSerializer : KSerializer<Angle> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Angle", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Angle) {
        encoder.encodeString("$value")
    }

    override fun deserialize(decoder: Decoder): Angle {
        return decoder.decodeString().toFloat().deg
    }
}

@Serializer(forClass = Temperature::class)
object TemperatureSerializer : KSerializer<Temperature> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Temperature", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Temperature) {
        encoder.encodeString("$value")
    }

    override fun deserialize(decoder: Decoder): Temperature {
        return decoder.decodeString().toFloat().kelvin
    }
}

@Serializer(forClass = Pressure::class)
object PressureSerializer : KSerializer<Pressure> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Pressure", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Pressure) {
        encoder.encodeString("$value")
    }

    override fun deserialize(decoder: Decoder): Pressure {
        return decoder.decodeString().toFloat().hpa
    }
}

@Serializer(forClass = Humidity::class)
object HumiditySerializer : KSerializer<Humidity> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Humidity", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Humidity) {
        encoder.encodeString("$value")
    }

    override fun deserialize(decoder: Decoder): Humidity {
        return decoder.decodeString().toFloat().percent
    }
}