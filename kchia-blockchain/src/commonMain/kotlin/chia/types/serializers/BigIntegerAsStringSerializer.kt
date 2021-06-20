package chia.types.serializers

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class BigIntegerAsStringSerializer : KSerializer<BigInteger> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BigInteger", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): BigInteger {
        return BigInteger.parseString(decoder.decodeString(), 10)
    }

    override fun serialize(encoder: Encoder, value: BigInteger) {
        encoder.encodeString(value.toString(10))
    }
}