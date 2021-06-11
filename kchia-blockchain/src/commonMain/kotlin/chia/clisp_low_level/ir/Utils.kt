@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.clisp_low_level.ir

import chia.clisp_low_level.elements.*
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.util.fromTwosComplementByteArray
import kotlin.sequences.first

object Utils {
    fun irNew(type: AtomOrPair, value: Any, offset: Int? = null): SExp {
        val x = if (offset != null) {
            (SExp to (type to offset))
        }  else {
            SExp to type
        }
        //println("IrNew: ${x.hex}, $value -> ${SExp to (x to value)}")
        return SExp to (x to value)
    }

    fun irCons(first: SExp, rest: SExp, offset: Int? = null): SExp {
        return irNew(TypeAtom.CONS.value, irNew(first, rest), offset)
    }


    fun irList(items: List<SExp>): SExp {
        return if (items.isNotEmpty()) {
            irCons(items[0], irList(items.drop(1)))
        } else irNull()
    }

    fun irNull(): SExp {
        return irNew(TypeAtom.NULL.value, 0)
    }

    fun irType(irSexp: SExp): BigInteger {
        var type = irSexp.first()
        if (type.listp()) {
            type = type.first()
        }

        return BigInteger.fromUByteArray(type.atom!!, Sign.POSITIVE)
    }

    fun irAsBig(irSexp: SExp): BigInteger {
        return BigInteger.fromUByteArray(irSexp.atom!!, Sign.POSITIVE)
    }

    fun irOffset(irSexp: SExp): BigInteger {
        val theOffset = irSexp.first()
        val x = if (theOffset.listp()) {
            theOffset.rest().atom!!
        } else {
            ubyteArrayOf(0xFFu)
        }
        return BigInteger.fromUByteArray(x, Sign.POSITIVE)
    }

    fun irVal(irSexp: SExp): SExp {
        return irSexp.rest()
    }

    fun irNullp(irSexp: SExp): Boolean {
        return irType(irSexp) == TypeAtom.NULL.value.num
    }

    fun irListp(irSexp: SExp): Boolean {
        return irType(irSexp) == TypeAtom.CONS.value.num
    }

    fun irAsSexp(irSexp: SExp): SExp {
        if (irNullp(irSexp)) {
            return SExp.__null__
        }
        if (irType(irSexp) == TypeAtom.CONS.value.num)
            return irAsSexp(irFirst(irSexp)).cons(irAsSexp(irRest(irSexp)))
        return irSexp.rest()
    }

    fun irIsAtom(irSexp: SExp): Boolean {
        return !(irListp(irSexp))
    }

    fun irAsAtom(irSexp: SExp): UByteArray {
        return irSexp.rest().atom!!
    }

    fun irFirst(irSexp: SExp): SExp = irSexp.rest().first()

    fun irRest(irSexp: SExp): SExp = irSexp.rest().rest()

    fun irSymbol(symbol: String): Pair<TypeAtom, UByteArray> {
        return Pair(TypeAtom.SYMBOL, symbol.encodeToByteArray().toUByteArray())
    }

    fun irAsSymbol(irSexp: SExp): String? {
        return if (irSexp.listp() && irType(irSexp) == TypeAtom.SYMBOL.value.num) {
            irAsSexp(irSexp).atom!!.toByteArray().decodeToString()
        } else {
            null
        }
    }

    fun irIter(irSexp: SExp): Iterator<SExp> = object : Iterator<SExp> {

        var tmp = irSexp
        override fun hasNext(): Boolean {
            return irListp(irSexp)
        }

        override fun next(): SExp {
            val first = irFirst(tmp)
            tmp = irRest(tmp)
            return first
        }
    }

    fun isIr(sexp: SExp): Boolean {
        if (sexp.nullp() || !sexp.listp()) {
            return false
        }

        if (sexp.first().listp()) {
            return false
        }

        val f = sexp.first().atom

        if (f == null || f.size > 1) {
            return false
        }

        val theType = BigInteger.fromTwosComplementByteArray(f.toByteArray())
        val t = toTypeAtom(theType) ?: return false

        val r =sexp.rest()

        if (t == TypeAtom.CONS) {
            if (r.nullp())
                return true
            if (r.listp())
                return isIr(r.first()) && isIr(r.rest())
            else
                return false
        }
        return !r.listp()

    }
}