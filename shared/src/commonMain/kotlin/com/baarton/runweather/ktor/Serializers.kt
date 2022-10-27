package com.baarton.runweather.ktor

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

@OptIn(ExperimentalSerializationApi::class)
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