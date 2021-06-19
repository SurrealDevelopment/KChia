@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.types.serializers

import bls.G2Element
import bls.G2FromBytes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import util.hexstring.asHexString
import util.hexstring.toHexString

class G2ElementSerializer: KSerializer<G2Element> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("hex", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): G2Element {
        return G2FromBytes(decoder.decodeString().asHexString().toByteArray())
    }

    override fun serialize(encoder: Encoder, value: G2Element) {
        encoder.encodeString(value.toUByteArray().toHexString())
    }
}