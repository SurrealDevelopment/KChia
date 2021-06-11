
@file:Suppress("EXPERIMENTAL_API_USAGE")
package chia.clisp_high_level.s2

import chia.types.NodePath
import chia.clisp_high_level.s2.S2Compile.quote
import chia.clisp_low_level.assemble
import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.elements.listp
import chia.clisp_low_level.elements.nullp
import chia.clisp_low_level.elements.rest
import chia.clisp_low_level.match
import chia.clisp_low_level.ops.Operators.KEYWORD_TO_ATOM
import com.ionspin.kotlin.bignum.integer.BigInteger

object S2Optimize {

    class Optimizer(val name: String, private val lambda: (SExp, Evaluator) -> SExp) {
        fun optimize(r: SExp, eval: Evaluator): SExp = lambda(r, eval)
    }

    private val CONS_OPTIMIZER_PATTERN_FIRST = assemble("(f (c (: . first) (: . rest)))")
    private val CONS_OPTIMIZER_PATTERN_REST = assemble("(r (c (: . first) (: . rest)))")

    private val APPLY_KW = KEYWORD_TO_ATOM["a"]!!
    private val FIRST_KW = KEYWORD_TO_ATOM["f"]!!
    private val REST_KW = KEYWORD_TO_ATOM["r"]!!
    private val CONS_KW = KEYWORD_TO_ATOM["c"]!!
    private val RAISE_KW = KEYWORD_TO_ATOM["x"]!!
    private val QUOTE_ATOM = KEYWORD_TO_ATOM["q"]!!
    private val APPLY_ATOM = KEYWORD_TO_ATOM["a"]!!

    private val debugOptimization = false

    private fun nonNil(sExp: SExp) =  (sExp.listp() || sExp.atom!!.size > 0)

    val consOptimizer = Optimizer("cons") { r, eval ->
        val t1 = match(CONS_OPTIMIZER_PATTERN_FIRST, r)
        if (t1 != null) {
            return@Optimizer t1["first"]!!
        }
        val t2 = match(CONS_OPTIMIZER_PATTERN_REST, r)
        if (t2 != null) {
            return@Optimizer t2["rest"]!!
        }
        return@Optimizer r

    }

    private fun seemsConstant(sExp: SExp): Boolean {
        if (!sExp.listp()) return !nonNil(sExp)

        val operator = sExp.first()
        if (!operator.listp()) {
            val atom = operator.asBig()
            if (atom == QUOTE_ATOM) return true
            if (atom == RAISE_KW) return false
        } else if (!seemsConstant(operator)) {
            return false
        }
        return sExp.rest().all { seemsConstant(it) }
    }

    private val constantOptimizer = Optimizer("constant") { r, eval ->
        /*
        If the expression does not depend upon @ anywhere,
            it's a constant. So we can simply evaluate it and
            return the quoted result.
         */
        if (seemsConstant(r) && nonNil(r)) {
            val e = eval.evaluate(r, SExp.__null__, null)
            return@Optimizer SExp to quote(e.second)
        } else {
            return@Optimizer r
        }
    }

    private fun isArgsCall(r: SExp): Boolean {
        return !r.listp() && r.asBig() == BigInteger.ONE
    }

    private val CONS_Q_A_OPTIMIZER_PATTERN = assemble("(a (q . (: . sexp)) (: . args))")

    private val consQAOptimzier = Optimizer("cons_q_a") { r, eval ->
        val t1 = match(CONS_Q_A_OPTIMIZER_PATTERN, r)
        return@Optimizer  if (t1 != null && isArgsCall(t1["args"]!!))
            t1["sexp"]!! else r
    }



    // Recursively apply optimizations to all non-quoted child nodes.
    private val childrenOptimizer = Optimizer("children_optimizer") { r, eval ->
        if (!r.listp()) return@Optimizer r
        val operator = r.first()
        if (!operator.listp()) {
            val op = operator.asBig()
            if (op == QUOTE_ATOM) {
                return@Optimizer r
            }
        }
        return@Optimizer SExp to r.map { optimizeSexp(it, eval) }.toList()
    }
    private val FIRST_ATOM_PATTERN = assemble("(f ($ . atom))")
    private val REST_ATOM_PATTERN = assemble("(r ($ . atom))")


    private val pathOptimizer = Optimizer("path") { r, eval ->
        val t1 = match(FIRST_ATOM_PATTERN,r)
        if (t1 != null && nonNil(t1["atom"]!!)) {
            var node = NodePath(t1["atom"]!!.asBig().intValue(true))
            node = node + NodePath.LEFT
            return@Optimizer SExp to node.shortPath
        }

        val t2 = match(REST_ATOM_PATTERN, r)
        if (t2 != null && nonNil(t2["atom"]!!)) {
            var node = NodePath(t2["atom"]!!.asBig().intValue(true))
            node = node + NodePath.RIGHT
            return@Optimizer SExp to node.shortPath
        }
        return@Optimizer r
    }

    private val QUOTE_PATTERN_1 = assemble("(q . 0)")

    private val quoteNullOptimizer = Optimizer("quoteNull") { r, eval ->
        val t1 = match(QUOTE_PATTERN_1, r)
        if (t1 != null) {
            SExp to 0
        } else {
            r
        }
    }

    private val APPLY_NULL_PATTERN_1 = assemble("(a 0 . (: . rest))")

    // `(a 0 ARGS)` => `0`
    private val nullOptimizer = Optimizer("null") { r, eval ->
        val t1 = match(APPLY_NULL_PATTERN_1, r)
        if (t1 != null)
            SExp.__null__
        else
            r
    }

    val OPTIMIZERS = listOf(
        consOptimizer,
        constantOptimizer,
        consQAOptimzier,
        childrenOptimizer,
        pathOptimizer,
        quoteNullOptimizer,
        nullOptimizer)

    /**
     * Optimize Sexp
     * TODO not fully developed
     */
    fun optimizeSexp(r: SExp, eval: Evaluator): SExp {
        if (r.nullp() || !r.listp()) {
            // cannot optimize further
            return r
        }
        var optimizedR  = r
        while (optimizedR.listp()) {
            val initial = optimizedR
            val optimizer = OPTIMIZERS.firstOrNull {
                optimizedR = it.optimize(optimizedR, eval)
                optimizedR != initial
            } ?: break

            if (debugOptimization) {
                println("OPT-${optimizer.name}: $initial -> $optimizedR")
            }
        }
        return optimizedR
    }
}