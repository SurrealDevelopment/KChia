package util.hexstring

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import kotlin.jvm.JvmInline


/**
 * See https://github.com/komputing/KHex
 */
@JvmInline
value class HexString(val string: String) {
    fun toByteArray(): ByteArray = decode(string)

    fun toBigInteger(): BigInteger = BigInteger.fromByteArray(this.toByteArray(), Sign.POSITIVE)

    @ExperimentalUnsignedTypes
    fun toUByteArray(): UByteArray = decodeUnsigned(string)

    fun has0xPrefix(): Boolean = string.startsWith("0x")

    fun prepend0xPrefix(): HexString = HexString(if (has0xPrefix()) string else "0x$string")
    fun clean0xPrefix(): HexString = HexString(if (has0xPrefix()) string.substring(2) else string)
    fun isValidHex(): Boolean = HEX_REGEX.matches(string)

    override fun toString(): String {
        // default return n o string
        return this.clean0xPrefix().string
    }
    companion object {
        internal val HEX_REGEX = Regex("(0[xX])?[0-9a-fA-F]*")
    }
}