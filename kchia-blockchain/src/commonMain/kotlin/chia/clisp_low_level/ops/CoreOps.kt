@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.clisp_low_level.ops

import chia.clisp_low_level.dissasemble
import chia.clisp_low_level.elements.*
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger

typealias OpRet = Pair<BigInteger, SExp>


// helper functions to build ops from lambdas
internal fun buildOp(opCode: BigInteger, name: String,  op: (args: SExp) -> OpRet): Op
    = buildOp(opCode, name, name, op)
internal fun buildOp(opCode: Int, name: String,  op: (args: SExp) -> OpRet): Op
        = buildOp(BigInteger(opCode), name, name, op)
internal fun buildOp(opCode: Int, rewriteSymbol: String, name: String,  op: (args: SExp) -> OpRet): Op
        = buildOp(BigInteger(opCode), rewriteSymbol, name, op)

internal fun buildOpByName(name: String, op: (args: SExp) -> OpRet = {Pair(BigInteger.ZERO, it)}) =
    buildOp(BigInteger.fromByteArray(name.encodeToByteArray(), Sign.POSITIVE), name, name, op)

internal fun buildOp(name: String,  op: (args: SExp) -> OpRet): Op
    = buildOp(null, name, name, op)
internal fun buildOp(rewriteSymbol: String, name: String,  op: (args: SExp) -> OpRet): Op
    = buildOp(null, rewriteSymbol, name, op)

internal fun buildOp(opCode: BigInteger?, rewriteSymbol: String,
                     opName: String, op: (args: SExp) -> OpRet): Op {
    return object : Op {
        override val opName: String = opName
        override val rewriteSymbol: String = rewriteSymbol
        override fun call(args: SExp): OpRet = op(args)
        override val opCode: BigInteger? = opCode

    }
}
internal interface Op {
    val rewriteSymbol: String
    val opName: String
    val opCode: BigInteger? // Op code may be null if the op code will be determined later
    fun call(args: SExp): OpRet
    fun asAtom(): UByteArray = opCode!!.toUByteArray()
}

internal interface OperatorLookupCallable {
    val quoteAtom: UByteArray
    val applyAtom: UByteArray
    fun call(op: UByteArray, args: SExp) : Pair<BigInteger, SExp>
}

internal object CoreOps {
    val opIf = buildOp(0x03,"i", "if") { args->
        if (args.listLen() != 3) {
            throw EvalError("if op takes 3 arguments: $args")
        }
        val r = args.rest()
        if (args.first().nullp()) {
            Pair(Costs.IF_COST.toBigInteger(), r.rest().first())
        } else {
            Pair(Costs.IF_COST.toBigInteger(), r.first())
        }
    }

    val opCons = buildOp(0x04,"c", "cons") { args->
        if (args.listLen() != 2) {
            throw EvalError("cons op takes 2 arguments: $args")
        }
        Pair(Costs.CONS_COST.toBigInteger(), args.first().cons(args.rest().first()))
    }

    val opFirst = buildOp(0x05,"f", "first") { args->
        if (args.listLen() != 1) {
            throw EvalError("first op takes 1 arguments: $args")
        }
        Pair(Costs.FIRST_COST.toBigInteger(), args.first().first())
    }


    val opRest = buildOp(0x06,"r", "rest") { args->
        if (args.listLen() != 1) {
            throw EvalError("rest op takes 1 arguments: $args")
        }
        Pair(Costs.REST_COST.toBigInteger(), args.first().rest())
    }

    val opListP = buildOp(0x07,"l", "listp") { args->
        if (args.listLen() != 1) {
            throw EvalError("l op takes 1 arguments: $args")
        }
        Pair(Costs.LISTP_COST.toBigInteger(), if (args.first().listp()) SExp.__true__ else SExp.__false__)
    }

    val opRaise = buildOp(0x08,"x", "raise") { args->
        throw EvalError("CLVM Raise: ${args.hex}")
    }

    val opEq = buildOp(0x09,"=", "eq") { args ->
        if (args.listLen() != 2) {
            throw EvalError("= op takes 2 arguments: $args")
        }
        val a0 = args.first()
        val a1 = args.rest().first()
        if (a0.pair != null || a1.pair != null) {
            throw EvalError("= does not work on lisp lists")
        }
        val b0 = a0.atom!!
        val b1 = a1.atom!!
        val cost = Costs.EQ_BASE_COST + (b0.size + b1.size) * Costs.EQ_COST_PER_BYTE

        Pair(cost.toBigInteger(), if (b0.contentEquals(b1)) SExp.__true__ else SExp.__false__)
    }

    val list = listOf(opIf, opCons, opFirst, opRest, opListP, opRaise, opEq)

}