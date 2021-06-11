package chia.clisp_high_level.s2

import chia.clisp_low_level.elements.SExp
import com.ionspin.kotlin.bignum.integer.BigInteger

// general format for some evaluation
abstract class Evaluator {
    abstract fun evaluate(program: SExp, args: SExp, maxCost: BigInteger? = null): Pair<BigInteger, SExp>
}


open class CompileBinding(val name: String,
    private val lambda: (args: SExp, macroLookup: SExp, symbolTable: SExp, evaluator: Evaluator, level: Int)
    -> SExp) {
    fun call(args: SExp, macroLookup: SExp, symbolTable: SExp, evaluator: Evaluator, level: Int): SExp {
        return lambda(args,macroLookup,symbolTable,evaluator, level)
    }

    override fun toString(): String {
        return "CompileBinding($name)"
    }
}