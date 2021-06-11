package bls

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import util.hexstring.uByteArrayFrom
import kotlin.jvm.JvmInline

// Simply random integer between 1 and group order
@JvmInline
@ExperimentalUnsignedTypes
value class PrivateKey(val value: BigInteger) {

    init {
        if (value >= defaultEc.n) {
            throw IllegalArgumentException("$value\nis more than group order\n${defaultEc.n}")
        }
    }

    fun getG1(): JacobianPoint = G1Generator() * value

    override fun toString(): String {
        return "PrivateKey(${value.toString(16)})"
    }

    fun toUByteArray(): UByteArray = value.toUByteArray()

    val size: Int get() = PRIVATE_KEY_SIZE


    companion object {
        val PRIVATE_KEY_SIZE = 32
        fun fromByteArray(bytes: ByteArray): PrivateKey {
            return PrivateKey(BigInteger.fromByteArray(bytes, Sign.POSITIVE).mod(defaultEc.n))
        }
        fun fromUByteArray(bytes: UByteArray): PrivateKey {
            return PrivateKey(BigInteger.fromUByteArray(bytes, Sign.POSITIVE).mod(defaultEc.n))
        }

        fun fromSeed(seed: UByteArray): PrivateKey {
            val L = 48
            val okm = Hkdf.extractExpand(
                L,
                seed + ubyteArrayOf(0u),
                "BLS-SIG-KEYGEN-SALT-".encodeToByteArray().toUByteArray(),
                ubyteArrayOf(0u, L.toUByte())
            )
            return PrivateKey(BigInteger.fromUByteArray(okm, Sign.POSITIVE).mod(defaultEc.n))
        }

        fun aggregate(privateKeys: List<PrivateKey>): PrivateKey {
            return PrivateKey(privateKeys.map { it.value }
                .reduce { acc, bigInteger -> acc + bigInteger }
                .mod(defaultEc.n))
        }
    }
}