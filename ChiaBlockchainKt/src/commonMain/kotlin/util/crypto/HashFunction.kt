package util.crypto

interface HashFunction {

    // in Bytes not bits
    val digestSize: Int
    val blockSize: Int

    fun digest(data: ByteArray): ByteArray
    fun digest(data: UByteArray): UByteArray
}