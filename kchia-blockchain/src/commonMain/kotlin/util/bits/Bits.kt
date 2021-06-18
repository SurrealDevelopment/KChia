package util.bits

import util.extensions.readULong
import kotlin.jvm.JvmInline
import kotlin.time.measureTime

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
    fun firstULongRight(): ULong {
        if (size > 64) throw Exception("$size too large for 64 bit ulong")

        return this.toRightArray().readULong()
    }

    fun firstULongLeft(): ULong {
        if (size > 64) throw Exception("$size too large for 64 bit ulong")

        return this.toLeftArray().take(8).toUByteArray().readULong()
    }

    val bytesNeeded: Int get() =  bits.size / 8 +
            if (bits.size.rem(8) > 0) 1 else 0




    // assume size is divisible by 8
    private fun justConvertArray(): UByteArray {
        if (this.size.rem(8) != 0) throw IllegalStateException()
        val arr = UByteArray(bytesNeeded)
        for (index in (bytesNeeded - 1) downTo 0) {
            if (bits[8*index + 0]) arr[index] = arr[index] or (0b10000000u)
            if (bits[8*index + 1]) arr[index] = arr[index] or (0b1000000u)
            if (bits[8*index + 2]) arr[index] = arr[index] or (0b100000u)
            if (bits[8*index + 3]) arr[index] = arr[index] or (0b10000u)
            if (bits[8*index + 4]) arr[index] = arr[index] or (0b1000u)
            if (bits[8*index + 5]) arr[index] = arr[index] or (0b100u)
            if (bits[8*index + 6]) arr[index] = arr[index] or (0b10u)
            if (bits[8*index + 7]) arr[index] = arr[index] or (0b1u)
        }
        return arr
    }

    // left is first bit
    fun toLeftArray(): UByteArray {
        // pad needed 0 bits to right
        val paddedBits = this + Bits((bytesNeeded * 8) - this.size)
        return paddedBits.justConvertArray()
    }

    // right is first bit
    fun toRightArray(): UByteArray {

        // pad needed 0 bits to left
        val paddedBits =  Bits((bytesNeeded * 8) - this.size) + this
        return paddedBits.justConvertArray()

    }

    companion object {

        private fun justConvert(uByteArray: UByteArray): BooleanArray {
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

            return arr
        }

        // Counts bit from right side of array to left
        // This is generally a natural conversion between byte arrays and bits.
        // while we assume Big Endianess the way the underlying byte array is storing the bytes may differ
        // (0x1, 0x0) size 10 = 0b0100000000
        fun fromRightArray(uByteArray: UByteArray, bitSize: Int = uByteArray.size *8): Bits {

            val arr = justConvert(uByteArray)
            // then trim, pad as needed
            val t = arr.takeLast(bitSize).toBooleanArray()
            val pad = if (t.size < bitSize)
            // pad to start
                BooleanArray(bitSize - t.size) + t
            else t

            return Bits(pad)
        }

        // Counts bit from left to right
        // (0x1, 0x0) size 10 = 0b0000000100
        fun fromLeftArray(uByteArray: UByteArray, bitSize: Int = uByteArray.size *8): Bits {
            val arr = justConvert(uByteArray)

            // then trim, pad as needed
            val t = arr.take(bitSize).toBooleanArray()
            val pad = if (t.size < bitSize)
            // pad to end
                 t + BooleanArray(bitSize - t.size)
            else t

            return Bits(pad)
        }
    }

}