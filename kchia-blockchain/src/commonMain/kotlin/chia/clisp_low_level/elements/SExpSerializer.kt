@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.clisp_low_level.elements

import chia.clisp_low_level.OpStackCallable
import chia.clisp_low_level.OpStackType
import chia.clisp_low_level.ValStackType
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import util.extensions.*
import util.hexstring.HexString
import util.hexstring.toHexString


@ExperimentalUnsignedTypes
internal class SExpSerializer : KSerializer<SExp> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("sexp", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): SExp {
        val str = HexString(decoder.decodeString())
        val bytes = str.toUByteArray()
        return SexpStreamDeserilizer(bytes.iterator()).getSexp()
    }

    override fun serialize(encoder: Encoder, value: SExp) {
        val str = SExpSerailization.SexpByteSequence(value).flatten().toList().toUByteArray().toHexString()

        encoder.encodeString(str)
    }


}

internal object SExpSerailization {

    val MAX_SINGLE_BYTE = 0x7fu
    val CONS_BOX_MARKER = UByteArray(1) {
        0xffu
    }

    fun atomToSerializedBytes(atom: UByteArray): UByteArray {

        if (atom.isEmpty()) {
            return UByteArray(1) {
                (0x80).toUByte()
            }
        }
        if (atom.size == 1) {
            if (atom[0] < MAX_SINGLE_BYTE) {
                return atom
            }
        }
        val size = atom.size.toUInt()
        val sizeBlob = when {
            size < 0x40u -> {
                ubyteArrayOf((0x80u or size).toUByte())
            }
            size < 0x2000u -> {
                ubyteArrayOf((0xC0u or (size shr 8)).toUByte(), ((size shr 8) and 0xFFu).toUByte())
            }
            size < 0x100000u -> {
                ubyteArrayOf(
                    (0xE0u or (size shr 16)).toUByte(),
                    ((size shr 8) and 0xFFu).toUByte(),
                    ((size shr 0) and 0xFFu).toUByte()
                )
            }
            size < 0x8000000u -> {
                ubyteArrayOf(
                    (0xF0u or (size shr 24)).toUByte(),
                    ((size shr 16) and 0xFFu).toUByte(),
                    ((size shr 8) and 0xFFu).toUByte(),
                    ((size shr 0) and 0xFFu).toUByte()
                )
            }
            //size < 0x400000000u {
            //    uintArrayOf(
            //        0xF8u or (size shr 32),
            //        (size shr 24) and  0xFFu,
            //        (size shr 16) and  0xFFu,
            //        (size shr 8) and  0xFFu,
            //        (size shr 0) and  0xFFu
            //    )
            //}
            else -> {
                throw IllegalArgumentException("Sexp too long" + size)
            }
        }

        return listOf(sizeBlob, atom).flatten().toUByteArray()
    }

    internal class SexpByteSequence(private val sexp: SExp) : Sequence<UByteArray> {


        override fun iterator(): Iterator<UByteArray> {
            return object : Iterator<UByteArray> {
                val todoStack = ArrayList<AtomOrPair>()

                init {
                    todoStack.push(this@SexpByteSequence.sexp)
                }

                override fun hasNext(): Boolean {
                    return this.todoStack.isNotEmpty()
                }

                override fun next(): UByteArray {
                    if (!hasNext()) throw IllegalStateException()
                    val sexp = this.todoStack.pop()
                    val pair = sexp.pair
                    if (pair != null) {
                        this.todoStack.push(pair.second)
                        this.todoStack.push(pair.first)
                        return CONS_BOX_MARKER

                    } else {
                        return atomToSerializedBytes(sexp.atom!!)
                    }

                }
            }
        }

    }


}

internal class SexpStreamDeserilizer(private val iter: Iterator<UByte>) {

    val atomFromStream: (bf: UInt) -> SExp = { bfi ->
        var bf = bfi
        if (bf == 0x80u) {
            SExp.to(UByteArray(0) { 0u })
        } else if (bf <= SExpSerailization.MAX_SINGLE_BYTE) {
            SExp.to(ubyteArrayOf(bf.toUByte()))
        } else {
            var bit_count = 0
            var bit_mask = 0x80u
            while (bf and bit_mask > 0u) {
                bit_count += 1
                bf = bf and (0xffu).pow(bit_mask.toInt())
                bit_mask = bit_mask shl 1
            }
            var sizeBlob = ubyteArrayOf(bf.toUByte())

            if (bit_count > 1) {
                val b = iter.take(bit_count - 1)
                sizeBlob = listOf(sizeBlob, b).flatten().toUByteArray()
            }
            val size = sizeBlob.readULong()
            if (size >= 0x400000000uL) {
                throw IllegalStateException("Blob too big: $size")
            }
            val blob = iter.take(size.toInt())
            SExp.to(blob)

        }

    }
    val opCons = OpStackCallable("cons") { _, valStack ->
        val right = valStack.pop()
        val left = valStack.pop()
        valStack.push(SExp.to(Pair(left, right)))
        BigInteger.ZERO
    }

    val opReadSexp: OpStackCallable = object : OpStackCallable("ReadSexp") {
        override fun call(opStack: OpStackType, valStack: ValStackType): BigInteger {
            val b = iter.next()
            if (b == (0xFFu).toUByte()) {
                opStack.push(opCons)
                opStack.push(this)
                opStack.push(this)
            } else {
                valStack.push(atomFromStream(b.toUInt()))
            }
            return BigInteger.ZERO
        }
    }


    fun getSexp(): SExp {
        val opStack: OpStackType = ArrayList()
        opStack.push(this.opReadSexp)
        val valStack: ValStackType = ArrayList()
        while (opStack.isNotEmpty()) {
            val func = opStack.pop()
            func.call(opStack, valStack)
        }
        return SExp to valStack.pop()
    }
}



