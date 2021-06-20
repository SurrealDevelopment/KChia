package chia.types.serializers

import chia.types.blockchain.Program
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ProgramAsBytesSerializer: KSerializer<Program> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Program")

    override fun deserialize(decoder: Decoder): Program {
        val size = decoder.decodeInt() // 4 bytes?
        var bytes = byteArrayOf()
        for (i in 0 until size) {
            bytes += decoder.decodeByte()
        }
        return Program.fromByteArray(bytes)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun serialize(encoder: Encoder, value: Program) {
        val bytes = value.toByteArray()
        encoder.encodeInt(bytes.size)
        for (element in bytes) {
            encoder.encodeByte(element.toByte())
        }
    }
}