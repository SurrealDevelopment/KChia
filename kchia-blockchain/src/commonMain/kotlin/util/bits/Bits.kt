package util.bits

import util.extensions.readULong
import util.extensions.toBytes
import kotlin.jvm.JvmInline

/**
 * Represent bits (encoded as boolean array
 * Not very efficient but gets job done
 *
 * !! Not optimized !!!
 */
@Suppress("EXPERIMENTAL_API_USAGE")
@JvmInline
value class Bits(val bits: BooleanArray) {

    constructor(): this(BooleanArray(0))
    constructor(size: Int): this(BooleanArray(size) { false })

    override fun toString(): String {
        return bits.joinToString("","b") {
            if (it) "1" else "0"
        }
    }

    fun contentEquals(other: Bits): Boolean = bits.contentEquals(other.bits)

    fun take(n: Int): Bits {
        return Bits(bits.take(n).toBooleanArray())
    }

    fun take(n: UInt): Bits {
        return Bits(bits.take(n.toInt()).toBooleanArray())
    }

    fun drop(n: Int): Bits {
        return Bits(bits.drop(n).toBooleanArray())
    }

    fun drop(n: UInt): Bits {
        return Bits(bits.drop(n.toInt()).toBooleanArray())
    }

    val size: Int get() = bits.size


    fun slice(range: IntRange) = Bits(bits.slice(range).toBooleanArray())

    fun slice(range: UIntRange) = Bits(bits.slice(range.map { it.toInt() }).toBooleanArray())

    // concatenate
    operator fun plus(other: Bits): Bits {
        return Bits(this.bits + other.bits)
    }

    /**
     * Reads up to a u long starting at the end of this
     */
    fun firstULong(): ULong {
        if (size > 64) throw Exception("$size too large for 64 bit ulong")

        return this.toUByteArray().readULong()
    }

    /**
     * Reads up to a signed long starting at the end of this
     */
    fun firstLong(): Long {
        if (size > 64) throw Exception("$size too large for 64 bit long")

        return this.toUByteArray().readULong().toLong()
    }

    fun toUByteArray(): UByteArray {
        val sizeNeeded = bits.size / 8 +
                if (bits.size.rem(8) > 0) 1 else 0

        val arr = UByteArray(sizeNeeded)
        // pad needed 0 bits to front
        val paddedBits = Bits((sizeNeeded * 8) - this.size) + this

        for (index in (sizeNeeded - 1) downTo 0) {
            if (paddedBits.bits[8*index + 0]) arr[index] = arr[index] or (0b10000000u)
            if (paddedBits.bits[8*index + 1]) arr[index] = arr[index] or (0b1000000u)
            if (paddedBits.bits[8*index + 2]) arr[index] = arr[index] or (0b100000u)
            if (paddedBits.bits[8*index + 3]) arr[index] = arr[index] or (0b10000u)
            if (paddedBits.bits[8*index + 4]) arr[index] = arr[index] or (0b1000u)
            if (paddedBits.bits[8*index + 5]) arr[index] = arr[index] or (0b100u)
            if (paddedBits.bits[8*index + 6]) arr[index] = arr[index] or (0b10u)
            if (paddedBits.bits[8*index + 7]) arr[index] = arr[index] or (0b1u)
        }

        return arr
    }

    companion object {

        fun fromUInt(uLong: UInt): Bits = Bits.fromByteArray(uLong.toBytes(8))
        fun fromULong(uLong: ULong): Bits = Bits.fromByteArray(uLong.toBytes(8))
        fun fromByteArray(uByteArray: UByteArray, bitSize: Int = uByteArray.size *8): Bits {

            val arr = BooleanArray(uByteArray.size * 8)
            // direct convert first
            uByteArray.forEachIndexed { index, uByte ->
                arr[8*index + 7] = uByte.and(0b1u) > 0u
                arr[8*index + 6] = uByte.and(0b10u) > 0u
                arr[8*index + 5] = uByte.and(0b100u) > 0u
                arr[8*index + 4] = uByte.and(0b1000u) > 0u
                arr[8*index + 3] = uByte.and(0b10000u) > 0u
                arr[8*index + 2] = uByte.and(0b100000u) > 0u
                arr[8*index + 1] = uByte.and(0b1000000u) > 0u
                arr[8*index + 0] = uByte.and(0b10000000u) > 0u // MSB
            }
            // then trim, pad as needed
            val t = arr.takeLast(bitSize).toBooleanArray()
            val pad = if (t.size < bitSize)
            // pad to start
                BooleanArray(t.size - bitSize) + t
            else t

            return Bits(pad)
        }
    }

}