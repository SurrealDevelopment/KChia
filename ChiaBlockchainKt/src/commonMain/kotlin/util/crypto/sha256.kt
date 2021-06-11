package util.crypto

import org.komputing.khash.sha256.Sha256


class Sha256: HashFunction {
    override val blockSize: Int
        get() = 512 /8
    override val digestSize: Int
        get() = 256 /8

    override fun digest(data: UByteArray): UByteArray {
        return Sha256.digest(data.toByteArray()).toUByteArray()
    }

    override fun digest(data: ByteArray): ByteArray {
        return Sha256.digest(data)
    }
}