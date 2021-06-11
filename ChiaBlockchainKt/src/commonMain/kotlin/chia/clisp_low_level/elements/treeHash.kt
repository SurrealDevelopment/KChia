@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.clisp_low_level.elements

import chia.clisp_low_level.OpStackCallable
import chia.clisp_low_level.OpStackType
import chia.clisp_low_level.ValStackType
import com.ionspin.kotlin.bignum.integer.BigInteger
import util.crypto.Sha256
import util.extensions.pop
import util.extensions.push


private class Functions(val precalculated: Set<UByteArray>) {
    val ONE = UByteArray(1) {
        (1).toUByte()
    }
    val TWO = UByteArray(1) {
        (2).toUByte()
    }
    lateinit var handleSexp: OpStackCallable
    lateinit var roll: OpStackCallable
    lateinit var handlePair: OpStackCallable

    init {
        handleSexp = OpStackCallable("handleSexp") { opStack, valStack ->
            val sexp = valStack.pop()
            if (sexp.pair != null) {
                val pair = sexp.pair!!
                val p0 = SExp to pair.first
                val p1 = SExp to pair.second
                valStack.push(p0)
                valStack.push(p1)
                opStack.push(handlePair)
                opStack.push(handleSexp)
                opStack.push(roll)
                opStack.push(handleSexp)
            } else {
                val atom = sexp.atom!!
                val r = if (atom in precalculated) {
                    sexp.atom!!
                } else {
                    Sha256().digest(ONE + sexp.atom!!)
                }
                valStack.push(SExp(r))
            }
            BigInteger.ZERO
        }

        handlePair = OpStackCallable("HandlePair") { opStack, valStack ->
            val p0 = valStack.pop()
            val p1 = valStack.pop()
            valStack.push(SExp(Sha256().digest(TWO + p0.atom!! + p1.atom!!)))
            BigInteger.ZERO
        }

        roll = OpStackCallable("roll") { opStack, valStack ->
            val p0 = valStack.pop()
            val p1 = valStack.pop()
            valStack.push(p0)
            valStack.push(p1)
            BigInteger.ZERO
        }
    }

}

internal fun sha256TreeHash(sexpToHash: SExp, precalculated: Set<UByteArray> = setOf()): UByteArray {
    // hash values in pre calculated are assumed to be hased already

    val valStack: ValStackType = ArrayList()
    valStack.push(sexpToHash)
    val opStack: OpStackType = ArrayList()
    opStack.push(Functions(precalculated).handleSexp)

    while (opStack.isNotEmpty()) {
        val op = opStack.pop()
        op.call(opStack, valStack)
    }

    return valStack[0].atom!!

}