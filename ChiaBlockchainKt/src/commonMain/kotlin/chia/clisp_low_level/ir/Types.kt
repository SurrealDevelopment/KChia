@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.clisp_low_level.ir

import chia.clisp_low_level.elements.AtomOrPair
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.util.fromTwosComplementByteArray
import util.extensions.toTrimmed2sCompUbyteARray


class TypeAtomValue(val string: String): AtomOrPair {
    val num = BigInteger.fromByteArray(string.encodeToByteArray(), Sign.POSITIVE)

    override val atom: UByteArray
        get() = num.toUByteArray()

    override val atomOrPair: Any
        get() = atom

    override val pair: Pair<AtomOrPair, AtomOrPair>?
        get() = null

    override fun cons(right: AtomOrPair): AtomOrPair {
        return this
    }

    override fun toString(): String {
        return "SExp IR Type: $string"
    }
}

// guesses type based on the array
// probably not best way of doing things but eh
fun typeForAtom(from: UByteArray): TypeAtom {
    if (from.size > 2) {
        return try {
            from.toByteArray().decodeToString(throwOnInvalidSequence = true)

            TypeAtom.QUOTES
        } catch (e : Exception) {
            TypeAtom.HEX
        }

    }
    if (BigInteger.fromTwosComplementByteArray(from.toByteArray())
            .toTrimmed2sCompUbyteARray()
            .contentEquals(from) )
        return TypeAtom.INT
    return TypeAtom.HEX
}

fun toTypeAtom(from: BigInteger): TypeAtom? {
    return TypeAtom.values().find { it.value.num == from }
}
enum class TypeAtom(val value: TypeAtomValue): AtomOrPair by value {
    CONS(TypeAtomValue("CONS")),
    NULL(TypeAtomValue("NULL")),
    INT(TypeAtomValue("INT")),
    HEX(TypeAtomValue("HEX")),
    QUOTES(TypeAtomValue("QT")),
    DOUBLE_QUOTE(TypeAtomValue("DQT")),
    SINGLE_QUOTE(TypeAtomValue("SQT")),
    SYMBOL(TypeAtomValue("SYM")),
    OPERATOR(TypeAtomValue("OP")),
    CODE(TypeAtomValue("CODE")),
    NODE(TypeAtomValue("NODE"))


}