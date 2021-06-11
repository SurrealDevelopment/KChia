@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.types.blockchain

import chia.clisp_low_level.elements.AtomOrPair
import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.elements.SexpStreamDeserilizer
import chia.clisp_low_level.elements.toImp
import chia.clisp_low_level.ops.Operators
import chia.clisp_low_level.runProgram
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.jvm.JvmInline

@JvmInline
value class Program constructor(private val sexp: SExp) {

    fun toByteArray(): UByteArray = sexp.bytes
    fun treeHash(): UByteArray = sexp.treeHash
    val hex: String get() = sexp.hex


    fun run(args: SExp): SExp {
        return runWithCost(infiniteCost, args).second
    }

    fun runWithCost(cost: BigInteger, args: SExp): Pair<BigInteger, SExp> {
        val aorp = runProgram(sexp, args, Operators.OPERATOR_LOOKUP, cost, null)
        return Pair(aorp.first, SExp to aorp.second)
    }

    companion object {

        val infiniteCost = BigInteger.parseString("7FFFFFFFFFFFFFFF", 16)

        fun fromByteArray(byteArray: ByteArray): Program {
            return Program(SexpStreamDeserilizer(byteArray.toUByteArray().iterator()).getSexp())
        }

        infix fun to(that: SExp) = toImp(that)
        infix fun to(that: Pair<Any?, Any?>) = toImp(that)
        infix fun to(that: AtomOrPair) = toImp(that)
        infix fun to(that: ByteArray) = toImp(that)
        infix fun to(that: Int) = toImp(that)
        infix fun to(that: Long) = toImp(that)
        infix fun to(that: UInt) = toImp(that)
        infix fun to(that: ULong) = toImp(that)
        infix fun to(that: BigInteger) = toImp(that)
        infix fun to(that: String) = toImp(that)
        infix fun to(that: Iterable<Any>) = toImp(that)
        private fun toImp(that: Any): Program {
            return Program(SExp.toImp(that))
        }
    }
}



