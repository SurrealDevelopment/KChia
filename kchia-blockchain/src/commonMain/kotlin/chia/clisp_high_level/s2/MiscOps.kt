package chia.clisp_high_level.s2

import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.ops.Operators
import chia.clisp_low_level.ops.buildOpByName
import com.ionspin.kotlin.bignum.integer.BigInteger


internal object MiscOps {

    private val APPLY_KW = Operators.KEYWORD_TO_ATOM["a"]
    private val CONS_KW = Operators.KEYWORD_TO_ATOM["c"]

    val opCom = buildOpByName("com") {
        throw IllegalStateException("This should not be used like this")
    }
    val opOpt = buildOpByName("opt") {
        throw IllegalStateException("This should not be used like this")
    }

    val list = listOf(opCom, opOpt)



}