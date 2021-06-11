package chia.clisp_low_level

import chia.clisp_low_level.ir.Utils
import chia.clisp_low_level.ir.readIr
import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.ops.Operators

private fun assembleFromIr(irSExp: SExp,

): SExp {
    var keyword = Utils.irAsSymbol(irSExp)
    if (keyword != null) {
        if (keyword.first() == '#') {
            keyword = keyword.drop(1)
        }
        val atom = Operators.KEYWORD_TO_ATOM[keyword]
        if (atom != null) {
            return SExp to atom
        } else {
            return Utils.irVal(irSExp)
        }
    }
    if (!Utils.irListp(irSExp)) {
        return Utils.irVal(irSExp)
    }
    if (Utils.irNullp(irSExp)) {
        return SExp.__null__
    }

    // handle 'q'
    val first = Utils.irFirst(irSExp)
    //keyword = Utils.irAsSymbol(irSExp)
    //if (keyword == "q") {
    //    // TODO
    //
    //}
    val sexp1 = assembleFromIr(first)
    val sexp2 = assembleFromIr(Utils.irRest(irSExp))
    return sexp1.cons(sexp2)

}

fun assemble(string: String): SExp {
    val irSymbols = readIr(string)
    return assembleFromIr(irSymbols)
}