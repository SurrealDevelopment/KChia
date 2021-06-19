package chia.types.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import util.hexstring.asHexString
import util.hexstring.toHexString

/**
 * Accepts several formats ie AA, 0xAA, 0xaa are equivalent
 */
class UByteArraySerializer: KSerializer<UByteArray> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("hex", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UByteArray {
        return decoder.decodeString().asHexString().toUByteArray()
    }

    override fun serialize(encoder: Encoder, value: UByteArray) {
        encoder.encodeString(value.toHexString())
    }
}