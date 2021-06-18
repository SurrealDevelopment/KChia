@file:Suppress("EXPERIMENTAL_API_USAGE")

package util.extensions

fun UInt.pow(n: Int): UInt {
    var i = 1u
    for (x in 0 until n) {
        i *= this
    }
    return i
}

/**
 * Reads up to a long from this byte array
 * Assumes big endian so it will read from right side
 */
fun UByteArray.readULong(): ULong {
    var result = 0uL
    var multiplier = 1uL
    val end = if (this.size <= 8) 0 else this.size - 8
    for (i in (this.size - 1) downTo end) {
        result += (multiplier * this[i])
        multiplier *= 256u
    }
    return result
}


/**
 * Reads up to a int from this byte array
 * Assumes big endian
 */
fun UByteArray.readUInt(): UInt {
    var result = 0u
    var multiplier = 1u
    val end = if (this.size <= 4) 0 else this.size - 4
    for (i in (this.size - 1) downTo end) {
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