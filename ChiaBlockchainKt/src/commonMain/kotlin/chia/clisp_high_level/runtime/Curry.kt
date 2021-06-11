package chia.clisp_high_level.runtime

import chia.clisp_low_level.assemble
import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.match
import chia.clisp_low_level.runProgram
import com.ionspin.kotlin.bignum.integer.BigInteger
import util.extensions.push

private val CURRY_OBJ_CODE_S = "(a (q #a 4 (c 2 (c 5 (c 7 0)))) (c (q (c (q . 2) (c (c (q . 1) 5) (c (a 6 (c 2 (c 11 (q 1)))) 0))) #a (i 5 (q 4 (q . 4) (c (c (q . 1) 9) (c (a 6 (c 2 (c 13 (c 11 0)))) 0))) (q . 11)) 1) 1))"
private val CURRY_OBJ_CODE = assemble(CURRY_OBJ_CODE_S)

fun curry(program: SExp, args: SExp): Pair<BigInteger, SExp> {
    val curryArgs = SExp to Pair(program, args)
    return runProgram(CURRY_OBJ_CODE, curryArgs)
}

private val UNCURRY_PATTERN_FUNCTION = assemble("(a (q . (: . function)) (: . core))")
private val UNCURRY_PATTERN_CORE = assemble("(c (q . (: . parm)) (: . core))")

// TODO, uncurry
fun uncurry(curriedProgram: SExp): Pair<SExp, SExp>? {
    var r = match(UNCURRY_PATTERN_FUNCTION, curriedProgram) ?: return null

    val f = SExp to (r["function"] ?: return null)
    var core = r["core"] ?: return null

    val args = ArrayList<SExp>()
    while(true) {
        r = match(UNCURRY_PATTERN_CORE, SExp to core) ?: break
        args.push(r["parm"] ?: return null)
        core = r["core"] ?: return null
    }

    return Pair(f, SExp to args)



}