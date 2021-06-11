@file:Suppress("EXPERIMENTAL_API_USAGE")
package chia.clisp_low_level.ir

import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.elements.SExpSerailization
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.util.fromTwosComplementByteArray
import util.hexstring.toHexString
import util.hexstring.toHexStringWithPrefix


private fun sexpFormatSequence(irSexp: SExp): Sequence<String> = sequence {
    yield("(")
    var isFirst = true
    var x = irSexp
    while (!Utils.irNullp(x)) {
        if (!Utils.irListp(x)) {
            yield(" . ")
            yieldAll(irFormatSequence(x))
            break
        }
        if (!isFirst) {
            yield(" ")
        }
        yieldAll(irFormatSequence(Utils.irFirst(x)))
        x = Utils.irRest(x)
        isFirst = false
    }
    yield(")")
}


private fun irFormatSequence(irSexp: SExp): Sequence<String> = sequence {
    if (Utils.irListp(irSexp)) {
        yieldAll(sexpFormatSequence(irSexp))
        return@sequence
    }
    val type = toTypeAtom(Utils.irType(irSexp)) ?: throw Exception("Could not determine IR Type: $irSexp")

    if (type == TypeAtom.CODE) {
        val str = SExpSerailization.SexpByteSequence(irSexp).flatten().toList().toUByteArray().toHexString()
        yield("CODE[$str]")
        return@sequence
    }

    if (type == TypeAtom.NULL) {
        yield("()")
        return@sequence
    }

    val atom = Utils.irAsAtom(irSexp)

    when(type) {
        TypeAtom.INT -> yield(BigInteger.fromTwosComplementByteArray(atom.toByteArray()).toString(10))
        TypeAtom.NODE -> yield("NODE[${BigInteger.fromTwosComplementByteArray(atom.toByteArray()).toString(10)}]")
        TypeAtom.HEX -> yield(atom.toHexStringWithPrefix("0x"))
        TypeAtom.QUOTES -> yield("\"${atom.toByteArray().decodeToString()}\"")
        TypeAtom.DOUBLE_QUOTE -> yield("\"${atom.toByteArray().decodeToString()}\"")
        TypeAtom.SINGLE_QUOTE -> yield("\'${atom.toByteArray().decodeToString()}\'")
        in listOf(TypeAtom.OPERATOR, TypeAtom.SYMBOL) -> {
            try {
                yield(atom.toByteArray().decodeToString())
            } catch (e: Exception) {
                yield("(indecipherable symbol: ${atom.toHexString()})")
            }
        } else -> throw Exception("Unknown IR Format: $type $irSexp")


    }
}
private fun writeIrToBuilder(irSexp: SExp, sb: StringBuilder) {
    irFormatSequence(irSexp).forEach {
        sb.append(it)
    }
}
fun writeIr(irSexp: SExp): String {
    val sb = StringBuilder()
    writeIrToBuilder(irSexp, sb)
    return sb.toString()
}