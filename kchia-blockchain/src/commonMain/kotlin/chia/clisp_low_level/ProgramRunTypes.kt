package chia.clisp_low_level

import chia.clisp_low_level.elements.SExp
import com.ionspin.kotlin.bignum.integer.BigInteger

/**
 * Types used for running chia lisp program
 * @see runProgram
 */


internal typealias ValStackType = ArrayList<SExp>

internal typealias ToSexpFun = (Any) -> SExp

internal typealias OpStackType = ArrayList<OpStackCallable>

internal typealias PreEvalOpFun = (opStack: OpStackType, valueStack: ValStackType) -> Unit

internal open class OpStackCallable(val name: String, private val l: (OpStackType, ValStackType) -> BigInteger) {
    constructor(l: (OpStackType, ValStackType) -> BigInteger) : this("nameless", l)
    constructor(name: String): this(name, {_,_ -> BigInteger.ZERO})
    open fun call(opStack: OpStackType, valStack: ValStackType): BigInteger = l(opStack, valStack)

    override fun toString(): String {
        return "OpStackCallable($name)"
    }
}

// special SAM to be called before evluation
fun interface PreEvalCallable {
    fun call(a: SExp, b: SExp): EvalContextCallable?
}

fun interface EvalContextCallable {
    fun call(a: SExp)
}