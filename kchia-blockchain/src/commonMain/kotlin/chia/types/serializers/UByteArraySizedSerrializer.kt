package chia.types.serializers


import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class UByteArraySizedSerrializer : KSerializer<UByteArray> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Bytes")

    override fun deserialize(decoder: Decoder): UByteArray {
        val size = decoder.decodeInt() // 4 bytes?
        var bytes = byteArrayOf()
        for (i in 0 until size) {
            bytes += decoder.decodeByte()
        }
        return bytes.toUByteArray()
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun serialize(encoder: Encoder, value: UByteArray) {
        encoder.encodeInt(value.size)
        for (element in value) {
            encoder.encodeByte(element.toByte())
        }
    }
}