@file:Suppress("EXPERIMENTAL_API_USAGE")

package util.extensions

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.util.toTwosComplementByteArray


/**
 * Converts an [Int] to an array of [Byte] using the big-endian conversion.
 * (The [Int] will be converted into 4 bytes)
 */
@ExperimentalUnsignedTypes
internal fun Int.toBytes(count: Int = 4): UByteArray {
    return this.toUInt().toBytes(count)

}
@ExperimentalUnsignedTypes
internal fun Long.toBytes(count: Int = 8): UByteArray {
    return this.toULong().toBytes(count)
}


internal fun BigInteger.highestOneBit(): Int {
    var c = 0
    var i = this
    while ( i > BigInteger.ZERO) {
        i = i shr 1
        c++
    }
    return c
}

internal fun BigInteger.bitCount(): Int {
    var i = this.highestOneBit()
    var c = 0
    while (i > 0) {
        c++
        i = i.shr(1)
    }
    return c
}


internal fun Int.bitCount(): Int {
    var i = this.takeHighestOneBit()
    var c = 0
    while (i > 0) {
        c++
        i = i.shr(1)
    }
    return c
}

internal fun Long.bitCount(): Int {
    var i = this.takeHighestOneBit()
    var c = 0
    while (i > 0) {
        c++
        i = i.shr(1)
    }
    return c
}


internal fun Int.bytesRequired(): Int {
    return (this.bitCount() + 7) shr 3
}

internal fun Long.bytesRequired(): Int {
    return (this.bitCount() + 7) shr 3
}

internal fun BigInteger.bytesRequired(): Int {
    return (this.bitCount() + 7) shr 3
}

@ExperimentalUnsignedTypes
internal fun UInt.toBytes(count: Int = 4): UByteArray {
    if (count > 4) throw IllegalArgumentException()
    val result = UByteArray(count)
    for (i in 0 until count) {
        result[i] = (this shr ((count - i - 1) *8)).toUByte()
    }
    return result
}

@ExperimentalUnsignedTypes
internal fun ULong.toBytes(count: Int = 8): UByteArray {
    if (count > 8) throw IllegalArgumentException()

    val result = UByteArray(count)
    for (i in 0 until count) {
        result[i] = (this shr ((count - i - 1)*8)).toUByte()
    }
    return result
}

@ExperimentalUnsignedTypes
fun BigInteger.toTrimmed2sCompUbyteARray(): UByteArray {
    if (this == BigInteger.ZERO) return ubyteArrayOf() // empty

    var array = this.toTwosComplementByteArray().toUByteArray()
    while (array.size > 1 && array.first() ==
        if (array[1].and((0x80u).toUByte()) > 0u)
            (0xffu).toUByte()
        else
            (0u).toUByte()) {
        array = array.drop(1).toUByteArray()
    }
    return array
}

@ExperimentalUnsignedTypes
fun UByteArray.trimSigned(): UByteArray {
    var array = this
    while (array.size > 1 && array.first() ==
        if (array[1].and((0x80u).toUByte()) > 0u)
                (0xffu).toUByte()
        else
            (0u).toUByte()) {
        array = array.drop(1).toUByteArray()
    }
    return array
}

fun ByteArray.trimSigned(): ByteArray {
    return this.toUByteArray().trimSigned().toByteArray()
}