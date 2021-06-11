package chia.clisp_low_level

import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.elements.listp
import chia.clisp_low_level.elements.nullp
import chia.clisp_low_level.elements.rest
import chia.clisp_low_level.ir.Utils
import chia.clisp_low_level.ir.typeForAtom
import chia.clisp_low_level.ir.writeIr
import chia.clisp_low_level.ops.Operators
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

fun dissasemble(sExp: SExp): String {
    val symbols = dissasembleToIr(sExp)
    return writeIr(symbols)
}

private fun dissasembleToIr(sExp: SExp, iAllowKeybaord: Boolean? = null): SExp {

    var allowKeyword = iAllowKeybaord
    if (Utils.isIr(sExp) && (allowKeyword == null || allowKeyword)) {
        return Utils.irCons(SExp to Utils.irSymbol("ir"), sExp)
    }

    if (sExp.listp()) {
        if (sExp.first().listp() || allowKeyword == null) allowKeyword = true

        val v0 = dissasembleToIr(sExp.first(), allowKeyword)

        val v1 = dissasembleToIr(sExp.rest(), false)
        return Utils.irCons(v0, v1)
    }

    val atom = sExp.atom!!
    if (allowKeyword != null && allowKeyword) {
        val atomBig = BigInteger.fromUByteArray(atom, Sign.POSITIVE)

        val v = Operators.KEYWORD_FROM_ATOM[atomBig]
        if ( v != null && v != ".")
            return SExp to Utils.irSymbol(v)
    }

    if (sExp.nullp()) {
        return Utils.irNull()
    }

    return SExp to Pair(typeForAtom(atom), atom)

}