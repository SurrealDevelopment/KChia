package chia.types.serializers

import bls.G1Element
import bls.G1FromBytes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class G1ElementAsBytesSerializer: KSerializer<G1Element> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("G1")

    override fun deserialize(decoder: Decoder): G1Element {
        val size = 48 // always
        var bytes = byteArrayOf()
        for (i in 0 until size) {
            bytes += decoder.decodeByte()
        }
        return G1FromBytes(bytes)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun serialize(encoder: Encoder, value: G1Element) {
        val bytes = value.toByteArray()
        for (element in bytes) {
            encoder.encodeByte(element)
        }
    }
}