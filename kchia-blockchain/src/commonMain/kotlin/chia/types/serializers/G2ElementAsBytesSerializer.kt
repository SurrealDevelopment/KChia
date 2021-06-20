package chia.types.serializers

import bls.G1Element
import bls.G1FromBytes
import bls.G2Element
import bls.G2FromBytes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class G2ElementAsBytesSerializer: KSerializer<G2Element> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("G2")

    override fun deserialize(decoder: Decoder): G2Element {
        val size = 96 // awlays 96
        var bytes = byteArrayOf()
        for (i in 0 until size) {
            bytes += decoder.decodeByte()
        }
        return G2FromBytes(bytes)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun serialize(encoder: Encoder, value: G2Element) {
        val bytes = value.toByteArray()
        for (element in bytes) {
            encoder.encodeByte(element)
        }
    }
}