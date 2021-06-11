@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.clisp_low_level

import chia.clisp_low_level.elements.*
import chia.clisp_low_level.elements.toImp
import chia.clisp_low_level.ops.Costs
import chia.clisp_low_level.ops.EvalError
import chia.clisp_low_level.ops.OperatorLookupCallable
import chia.clisp_low_level.ops.Operators
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import util.extensions.pop
import util.extensions.push
import util.hexstring.toHexString
import kotlin.sequences.first


private fun toPreEvalOp(preEvalF: PreEvalCallable, toSexpFun: ToSexpFun): PreEvalOpFun = { opStack, valueStack ->
    val v = toSexpFun(valueStack.last())
    preEvalF.call(v.first(), v.rest())?.let {
        opStack.add( OpStackCallable("Pre Eval") { _, _ ->
            it.call(toSexpFun(valueStack.last()))
            BigInteger.ZERO
        })
    }
}

private fun msbMask(uByte: UByte): UByte {
    var x = uByte.toInt()
    x = x or (x shr 1)
    x = x or (x shr 2)
    x = x or (x shr 4)
    return ((x + 1) shr 1).toUByte()

}

// default run program from strings
fun runFromString(program: String, args: String): Pair<BigInteger, SExp> {
    val p = assemble(program)
    val a = assemble(args)
    val run = runProgram(p, a, Operators.OPERATOR_LOOKUP)
    return run
}


internal fun runProgram(
    program: SExp,
    progArgs: SExp,
    operatorLookup: OperatorLookupCallable = Operators.OPERATOR_LOOKUP,
    maxCost: BigInteger? = null,
    preEvalF: PreEvalCallable? = null): Pair<BigInteger, SExp> {

    // setup pre eval op
    val preEvalOp = if (preEvalF != null) {
        toPreEvalOp(preEvalF) {
            SExp.toImp(it)
        }
    } else {
        null
    }

    fun traversePath(sexp: SExp, iEnv: SExp) : Pair<BigInteger, SExp> {
        //println("TRAVERSE: $sexp, $iEnv")
        var cost = Costs.PATH_LOOKUP_BASE_COST.toBigInteger()
        cost += Costs.PATH_LOOKUP_COST_PER_LEG

        if (sexp.nullp()) return cost to SExp.__null__

        val b = sexp.atom!!

        var endByteCursor = 0
        while (endByteCursor < b.size && b[endByteCursor].toUInt() == 0u) {
            endByteCursor += 1
        }

        cost += endByteCursor * Costs.PATH_LOOKUP_COST_PER_ZERO_BYTE
        if (endByteCursor == b.size) {
            return cost to SExp.__null__
        }

        // create a bit mask for most significant bit set
        // in the last non zero byte
        val endBitmask = msbMask(b[endByteCursor])

        var byteCursor = b.size - 1

        var bitMask: UInt = 0x01u

        var env = iEnv
        while (byteCursor < endByteCursor || bitMask < endBitmask) {
            if (env.pair == null) {
                throw EvalError("Path into atom! $env")
            }
            if (b[byteCursor].and(bitMask.toUByte()) > 0u) {
                env = env.rest()
            } else {
                env = env.first()
            }
            cost += Costs.PATH_LOOKUP_COST_PER_LEG

            bitMask = (bitMask.shl(1))
            if (bitMask == 0x100u) {
                byteCursor -= 1
                bitMask = 0x01u
            }
        }

        return cost to env
    }

    val swapOp = OpStackCallable("swap") { opStack, valStack ->

        val v2 = valStack.pop()
        val v1 = valStack.pop()

        valStack.push(v2)
        valStack.push(v1)
        BigInteger.ZERO

    }

    val consOp = OpStackCallable("cons") { opStack, valStack ->

        val v1 = valStack.pop()
        val v2 = valStack.pop()
        //println("CONS: v1=$v1, v2=$v2")

        valStack.push(v1.cons(v2))
        BigInteger.ZERO
    }

    lateinit var applyOp: OpStackCallable
    lateinit var evalOp: OpStackCallable

    evalOp = OpStackCallable("Eval") { opStack, valStack ->
        if (preEvalOp != null) preEvalOp(opStack, valStack)

        val pair = valStack.pop()
        val sexp = pair.first()
        val args = pair.rest()

        // put ops on stack
        if (sexp.pair == null) {
            val t = traversePath(sexp, args)
            valStack.push(t.second)
            return@OpStackCallable t.first
        }

        val operator = sexp.first()
        if (operator.pair != null) {

            val opPair = operator.pair!!
            if (opPair.first.pair != null || opPair.second != SExp.__null__) {
                throw Exception("in ((X)...) syntax X must be lone atom: $sexp")
            }
            val newOpList = sexp.rest()
            valStack.push(SExp to opPair.first)
            valStack.push(newOpList)
            opStack.push(applyOp)
            return@OpStackCallable Costs.APPLY_COST.toBigInteger()
        }
        val op =operator.atom!!
        var operandList = sexp.rest()

        if (op.contentEquals(operatorLookup.quoteAtom)) {
            valStack.push(operandList)
            return@OpStackCallable Costs.QUOTE_COST.toBigInteger()
        }

        opStack.push(applyOp)
        valStack.push(operator)
        while (!operandList.nullp()) {
            val z = operandList.first()
            valStack.push(z.cons(args))
            opStack.push(consOp)
            opStack.push(evalOp)
            opStack.push(swapOp)
            operandList = operandList.rest()
        }
        valStack.push(SExp.__null__)
        return@OpStackCallable BigInteger.ONE
    }

    applyOp = OpStackCallable("Apply") { opStack, valStack ->
        val operandList = valStack.pop()
        val operator = valStack.pop()

        if (operator.pair != null)
            throw Exception("Internal error")

        val op = operator.atom!!

        if (op.contentEquals(operatorLookup.applyAtom)) {
            if (operandList.count() != 2) {
                throw Exception("Apply requires exactly 2 arguments")
            }
            val newProgram = operandList.first()
            val newArgs = operandList.rest().first()
            valStack.push(newProgram.cons(newArgs))
            opStack.push(evalOp)
            return@OpStackCallable Costs.APPLY_COST.toBigInteger()
        }

        val r = operatorLookup.call(op, operandList)
        valStack.push(r.second)
        return@OpStackCallable r.first
    }

    // create initial stacks and start loop to run profram
    val opStack: OpStackType = ArrayList()
    opStack.push(evalOp)
    val valStack: ValStackType = ArrayList()
    valStack.push(program.cons(progArgs))
    var cost = BigInteger.ZERO

    while (opStack.isNotEmpty()) {
        val f = opStack.pop()

        cost += f.call(opStack, valStack)
        if (maxCost != null && cost > maxCost) {
            throw EvalError("Max cost of $maxCost exceeded.")
        }
    }
    return cost to valStack.last()
}