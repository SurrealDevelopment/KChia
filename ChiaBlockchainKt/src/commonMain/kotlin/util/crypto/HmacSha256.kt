package util.crypto

import util.hexstring.toHexString
import kotlin.experimental.xor

/**
 * Hmac is required to prevent length extension attacks
 */
object HmacSha256 {

    fun hmac(key: UByteArray, messaage:UByteArray): UByteArray {
        return hmac(key.toByteArray(), messaage.toByteArray()).toUByteArray()
    }

    fun hmac(key: ByteArray, messaage:ByteArray): ByteArray {
        val fn = Sha256()

        var paddedKey = if (key.size > fn.blockSize)
            fn.digest(key)
        else
            key

        paddedKey = if (paddedKey.size < fn.blockSize)
            paddedKey + ByteArray(fn.blockSize - paddedKey.size) { 0 }
        else
            paddedKey

        val oKeyPad = paddedKey.map { it.xor(0x5C) }.toByteArray()
        val iKeyPad = paddedKey.map { it.xor(0x36) }.toByteArray()

        return fn.digest(oKeyPad + fn.digest(iKeyPad + messaage))
    }
}