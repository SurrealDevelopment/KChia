@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.types.serializers

import bls.G1Element
import bls.G1FromBytes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import util.hexstring.asHexString
import util.hexstring.toHexString

class G1ElementSerializer: KSerializer<G1Element> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("hex", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): G1Element {
        return G1FromBytes(decoder.decodeString().asHexString().toByteArray())
    }

    override fun serialize(encoder: Encoder, value: G1Element) {
        encoder.encodeString(value.toUByteArray().toHexString())
    }
}