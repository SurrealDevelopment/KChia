@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.clisp_low_level.ops

import chia.clisp_low_level.elements.SExp
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import util.extensions.fastPow
import kotlin.jvm.JvmInline

class EvalError(msg: String) : Exception(msg)

internal object Operators {

    val keyOps = CoreOps.list + MoreOps.list + ShallowOps.list
    val keyOpMap = keyOps.map { Pair(it.opCode!!, it) }.toMap()
    val KEYWORD_FROM_ATOM = keyOps.map { Pair(it.opCode!!, it.rewriteSymbol) }.toMap()
    val KEYWORD_TO_ATOM = keyOps.map { Pair(it.rewriteSymbol, it.opCode!!) }.toMap()

    fun argsLen(opName: String, args: SExp): Sequence<Int> {
        return args.map {
            if (it.pair != null) throw Exception("$opName requires int args")
            it.atom!!.size
        }
    }

    // unknown ops are reserved if they start with 0xffff
    // otherwise, unknown ops are no-ops, but they have costs. The cost is computed
    // like this:

    // byte index (reverse):
    // | 4 | 3 | 2 | 1 | 0          |
    // +---+---+---+---+------------+
    // | multiplier    |XX | XXXXXX |
    // +---+---+---+---+---+--------+
    //  ^               ^    ^
    //  |               |    + 6 bits ignored when computing cost
    // cost_multiplier  |
    //                  + 2 bits
    //                    cost_function

    // 1 is always added to the multiplier before using it to multiply the cost, this
    // is since cost may not be 0.

    // cost_function is 2 bits and defines how cost is computed based on arguments:
    // 0: constant, cost is 1 * (multiplier + 1)
    // 1: computed like operator add, multiplied by (multiplier + 1)
    // 2: computed like operator mul, multiplied by (multiplier + 1)
    // 3: computed like operator concat, multiplied by (multiplier + 1)

    // this means that unknown ops where cost_function is 1, 2, or 3, may still be
    // fatal errors if the arguments passed are not atoms.
    fun defaultUnknownOp(op: UByteArray, args: SExp): Pair<BigInteger, SExp> {
        // OPCODE 0xFFFF is reserved
        if (op.isEmpty() || (op[0] == (0xFFu).toUByte() && op[1] == (0xFFu).toUByte())) {
            throw EvalError("Reserved Operator: ${args to op}")
        }

        val costFunction = (op.last().and(0b11000000u)).toUInt().shr(6)
        if (op.size > 5) throw EvalError("Invalid operator: ${SExp to op}")

        val costMultiplier = BigInteger.fromUByteArray(op.dropLast(1).toUByteArray()
            , Sign.POSITIVE) + 1

        // 0 = constant
        // 1 = like op_add/op_sub
        // 2 = like op_multiply
        // 3 = like op_concat

        val cost = when (costFunction) {
            0u -> {
                1
            }
            1u -> {
                var cost = Costs.ARITH_BASE_COST
                var argSize = 0
                argsLen("unknown op", args).forEach {
                    argSize+= it
                    cost+= Costs.ARITH_COST_PER_ARG
                }
                cost + argSize * Costs.ARITH_COST_PER_BYTE
            }
            2u -> {
                var cost = Costs.MUL_BASE_COST
                val operands = argsLen("unknown op", args)
                operands.reduce { acc, i ->
                    cost += Costs.MUL_COST_PER_OP
                    cost += (acc + i) * Costs.MUL_LINEAR_COST_PER_BYTE
                    cost += (acc * i) / Costs.MUL_SQUARE_COST_PER_BYTE_DIVIDER
                    acc + i
                }
            } 3u -> {
                var cost = Costs.CONCAT_BASE_COST
                val length = args.fold(0) { acc, arg ->
                    if (arg.pair != null) throw EvalError("unknwon op on list. $arg")
                    cost += Costs.CONCAT_COST_PER_ARG
                    acc + arg.atom!!.size
                }
                cost + length * Costs.CONCAT_COST_PER_BYTE
            }
            else -> throw EvalError("Illegal state for cost function")
        }.toBigInteger() * costMultiplier

        if (cost >= BigInteger.TWO.fastPow(32))
            throw EvalError("invalid oeprator ${SExp to op}")

        return cost to SExp.__null__
    }

    // wrapper between map and callable
    @JvmInline
    value class OperatorMap(val map: HashMap<BigInteger, Op> ): OperatorLookupCallable {

        override val quoteAtom: UByteArray
            get() = map[1.toBigInteger()]!!.asAtom()
        override val applyAtom: UByteArray
            get() = map[2.toBigInteger()]!!.asAtom()
        override fun call(op: UByteArray, args: SExp): Pair<BigInteger, SExp> {
            val opNum = BigInteger.fromUByteArray(op, Sign.POSITIVE)
            val f = map[opNum] ?: return defaultUnknownOp(op, args)
            return f.call(args)
        }

        companion object {
            fun from(other: OperatorMap): OperatorMap {
                return OperatorMap(HashMap(other.map)) // copies ref value
            }
        }
    }

    val OPERATOR_LOOKUP = OperatorMap(HashMap(keyOpMap))



}