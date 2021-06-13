@file:Suppress("EXPERIMENTAL_API_USAGE")

package util.extensions

import com.ionspin.kotlin.bignum.integer.BigInteger


fun UInt.pow(n: Int): UInt {
    var i = 1u
    for (x in 0 until n) {
        i *= this
    }
    return i
}

/**
 * Reads up to a long from this byte array
 */
fun UByteArray.readULong(): ULong {
    var result = 0uL
    var multiplier = 1uL
    val start = if (this.size > 8) 8 else this.size
    for (i in (start - 1) downTo 0) {
        result += (multiplier * this[i])
        multiplier *= 256u
    }
    return result
}

/**
 * Reads up to a int from this byte array
 */
fun UByteArray.readUInt(): UInt {
    var result = 0u
    var multiplier = 1u
    val start = if (this.size > 4) 4 else this.size
    println(this)
    for (i in (start - 1) downTo 0) {
        result += (multiplier * this[i])
        multiplier *= 256u
    }
    return result
}


fun Iterator<UByte>.take(n: Int): UByteArray {
    val b = UByteArray(n) { 0u }
    for (i in 0 until n) {
        b[i] = this.next()
    }
    return b
}