package chia.types.serializers

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.util.fromTwosComplementByteArray
import com.ionspin.kotlin.bignum.integer.util.toTwosComplementByteArray
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class BigIntegerAs128Serializer : KSerializer<BigInteger> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("uint128",)

    override fun deserialize(decoder: Decoder): BigInteger {
        val size = 16
        var bytes = byteArrayOf()
        for (i in 0 until size) {
            bytes += decoder.decodeByte()
        }
        return BigInteger.fromTwosComplementByteArray(bytes)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun serialize(encoder: Encoder, value: BigInteger) {
        val bigArr = value.toTwosComplementByteArray().toUByteArray()
        val pad = if (bigArr.size < 16) {
            if (value < 0) {
                UByteArray(16 - bigArr.size) {0xffu} + bigArr
            } else {
                UByteArray(16 - bigArr.size) {0x00u} + bigArr
            }
        } else if (bigArr.size == 16) {
            bigArr
        } else {
            throw IllegalArgumentException("Big integer too big. Size =${bigArr.size}")
        }

        for (i in 0 until 16) {
            encoder.encodeByte(pad[i].toByte())
        }
    }
}