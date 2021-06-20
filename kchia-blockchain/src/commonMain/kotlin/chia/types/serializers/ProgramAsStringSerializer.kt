package chia.types.serializers

import chia.types.blockchain.Program
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import util.hexstring.asHexString

class ProgramAsStringSerializer: KSerializer<Program> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ProgHex", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Program {
        val data =  decoder.decodeString().asHexString().toByteArray()
        return Program.fromByteArray(data)
    }

    override fun serialize(encoder: Encoder, value: Program) {
        encoder.encodeString(value.hex)
    }
}