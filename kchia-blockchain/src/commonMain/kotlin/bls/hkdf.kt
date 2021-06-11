package bls

import util.crypto.HmacSha256
import util.hexstring.toHexString
import kotlin.math.ceil

@ExperimentalUnsignedTypes
object Hkdf {

    val BLOCK_SIZE = 32
    fun extract(salt: UByteArray, ikm: UByteArray): UByteArray {
        val h =  HmacSha256.hmac(salt, ikm)
        return h
    }

    fun expand(L: Int, prk: UByteArray, info: UByteArray): UByteArray {
        val N: Int = ceil(L.toDouble() / BLOCK_SIZE).toInt()
        var bytesWritten = 0
        var okm = UByteArray(0)

        var T: UByteArray = ubyteArrayOf()
        for (i in 1..N) {
            if (i == 1) {
                T = HmacSha256.hmac(prk, info + ubyteArrayOf(1u))
            } else {
                T = HmacSha256.hmac(prk, T + info + ubyteArrayOf(i.toUByte()))
            }
            var toWrite = L - bytesWritten
            if (toWrite > BLOCK_SIZE)
                toWrite = BLOCK_SIZE
            okm += T.take(toWrite)
            bytesWritten += toWrite
        }
        if (bytesWritten != L) throw IllegalStateException("Bytes written not same")
        return okm
    }

    fun extractExpand(L: Int, key: UByteArray, salt: UByteArray, info: UByteArray): UByteArray {
        val prk = extract(salt, key)
        return expand(L, prk, info)
    }
}