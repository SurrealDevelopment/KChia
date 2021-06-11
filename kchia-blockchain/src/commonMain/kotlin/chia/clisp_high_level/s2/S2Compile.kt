@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.clisp_high_level.s2

import chia.clisp_low_level.dissasemble
import chia.clisp_low_level.ops.EvalError
import chia.clisp_low_level.ops.Operators
import chia.types.NodePath
import chia.clisp_low_level.elements.*
import chia.clisp_low_level.ops.buildOpByName
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import kotlin.sequences.first


object S2Compile {
    private val QUOTE_ATOM = Operators.KEYWORD_TO_ATOM["q"]!!
    private val APPLY_ATOM = Operators.KEYWORD_TO_ATOM["a"]!!
    private val CONS_ATOM = Operators.KEYWORD_TO_ATOM["c"]!!

    private val PASSTHRU_OPERATOR = ((Operators.KEYWORD_TO_ATOM.values) + MiscOps.list.map { it.opCode })
        .filterNotNull()

    // compile bindings
    lateinit var compileqq: CompileBinding
    var compileMacros: CompileBinding
    var compileSymbols: CompileBinding
    lateinit var compileMod: CompileBinding

    var compileBindings: Map<BigInteger, CompileBinding>

    init {
        compileqq = CompileBinding("qq") { args, macroLookup, symbolTable, runProgramF, level ->
            fun com(sExp: SExp) = doComProg(sExp, macroLookup, symbolTable, runProgramF)

            val sexp = args.first()
            if (!sexp.listp() || sexp.nullp()) {
                return@CompileBinding SExp to quote(sexp)
            } else if (sexp.listp() && !sexp.first().listp()) {
                val op = sexp.first().atom!!
                if (op.contentEquals("qq".encodeToByteArray().toUByteArray())) {
                    val subExp = compileqq.call(sexp.rest(), macroLookup, symbolTable, runProgramF, level+1)
                    val arg = SExp to listOf(CONS_ATOM,
                        op,
                        listOf(CONS_ATOM,
                            subExp,
                            quote(0)
                        )
                    )
                    return@CompileBinding com(arg)
                }
                else if (op.contentEquals("unquote".encodeToByteArray().toUByteArray())) {
                    if (level == 1) {
                        // (qq (unquote X)) => X
                        return@CompileBinding com(sexp.rest().first())
                    }
                    val subExp = compileqq.call(sexp.rest(), macroLookup, symbolTable, runProgramF, level-1)
                    return@CompileBinding com(SExp to listOf(
                        CONS_ATOM,
                        op,
                        listOf(
                            CONS_ATOM,
                            subExp,
                            quote(0)
                        )
                    ))
                }
            }
            val A = com(SExp to listOf("qq", sexp.first()))
            val B = com(SExp to listOf("qq", sexp.rest()))
            return@CompileBinding SExp to listOf(CONS_ATOM, A, B)
        }
        compileMacros = CompileBinding("macros") { _, macroLookup, _, _, _ ->
            return@CompileBinding SExp to quote(macroLookup)
        }

        compileSymbols = CompileBinding("symbols") { _, _, symbolTable, _, _ ->
            return@CompileBinding SExp to quote(symbolTable)
        }

        compileBindings = mapOf(
            Pair(buildOpByName("qq").opCode!!, compileqq),
            Pair(buildOpByName("macros").opCode!!, compileMacros),
            Pair(buildOpByName("symbols").opCode!!, compileSymbols),
            Pair(buildOpByName("lambda").opCode!!, Mod.compileMod),
            Pair(buildOpByName("mod").opCode!!, Mod.compileMod)

        )
    }




    fun quote(other: SExp): Pair<BigInteger, SExp> = Pair(QUOTE_ATOM, other)
    fun quote(other: Int): Pair<BigInteger, Int> = Pair(QUOTE_ATOM, other)
    fun quote(other: List<Any>): Pair<BigInteger, Any> {
        return Pair(QUOTE_ATOM, other)
    }
    fun eval(prog: SExp, args: Pair<BigInteger, SExp>) = SExp to listOf(APPLY_ATOM, prog, args)
    fun eval(prog: SExp, args: BigInteger) = SExp to listOf(APPLY_ATOM, prog, args)
    fun eval(prog: SExp, args: UByteArray) = SExp to listOf(APPLY_ATOM, prog, args)
    fun eval(prog: SExp, args: ByteArray) = SExp to listOf(APPLY_ATOM, prog, args)


    fun run(prog: SExp, macroLookup: SExp): SExp {
        val args = NodePath.TOP.shortPath
        val mac = quote(macroLookup)
        return eval(SExp to listOf("com", prog, mac), args)
    }
    fun brun(prog: SExp, args: SExp) = eval(SExp to quote(prog), quote(args))

    @Suppress("UNUSED_PARAMETER")
    private fun lowerQuote(prog: SExp, macroLookup: SExp? = null,
                           symbolTable: SExp? = null, runProgram: Evaluator? = null): SExp {
        return if (prog.nullp()) prog
        else if (prog.listp()) {
            if (prog.first().atom?.contentEquals("quote".encodeToByteArray().toUByteArray()) == true) {
                if (!prog.rest().rest().nullp()) {
                    throw EvalError("Compilation error compining ${dissasemble(prog)}. Quote takes 1 argument.")
                }
                SExp to quote(lowerQuote(prog.rest().first()))
            }
            else {
                SExp to Pair(lowerQuote(prog.first()), lowerQuote(prog.rest()))
            }
        } else {
            prog
        }
    }
    /**
     * Turn the given program `prog` into a clvm program using
     * the macros to do transformation.
     * prog is an uncompiled s-expression.
     * Return a new expanded s-expression PROG_EXP that is equivalent by rewriting
     * based upon the operator, where "equivalent" means
     * (a (com (q PROG) (MACROS)) ARGS) == (a (q PROG_EXP) ARGS)
     * for all ARGS.
     * Also, (opt (com (q PROG) (MACROS))) == (opt (com (q PROG_EXP) (MACROS)))
     */
    fun doComProg(progC: SExp, macroLookup: SExp, symbolTable: SExp, runProgram: Evaluator): SExp {

        val prog = lowerQuote(progC, macroLookup, symbolTable, runProgram)

        // quote atoms
        if (prog.nullp() || !prog.listp()) {
            val atom = prog.atom!!
            if (atom.contentEquals("@".encodeToByteArray().toUByteArray()))
                return SExp to NodePath.TOP.shortPath

            symbolTable.forEach {
                val symbol = it.first()
                val value = it.rest().first()
                if (symbol == prog) {
                     return SExp to value
                }
            }
            return SExp to quote(prog)
        }

        val operator = prog.first()
        if (operator.listp()) {
            val innnerExp = eval(SExp to listOf(
                "com",
                quote(operator),
                quote(macroLookup),
                quote(symbolTable)),
                NodePath.TOP.shortPath
            )
            return SExp to listOf(innnerExp)
        }

        val asAtom = operator.atom!!
        macroLookup.forEach {
            val macroName = it.first().atom!!

            if (asAtom.contentEquals(macroName)) {
                val macroCode = it.rest().first()
                val postProg = brun(macroCode, prog.rest())

                val e=  eval(
                    SExp to listOf(
                        "com",
                        postProg,
                        quote(macroLookup),
                        quote(symbolTable)
                    ), NodePath.TOP.shortPath
                )
                return e

            }
        }

        compileBindings[BigInteger.fromUByteArray(asAtom, Sign.POSITIVE)]?.let {
            val postProg = it.call(prog.rest(), macroLookup, symbolTable, runProgram, 1)
            val eval = eval(SExp to quote(postProg), NodePath.TOP.shortPath)
            return eval

        }

        if (operator == SExp to QUOTE_ATOM) {
            return prog
        }

        val compiledArgs = prog.rest().map {
            doComProg(it, macroLookup, symbolTable, runProgram)
        }.toList()

        val r = SExp to (listOf(operator) + compiledArgs)


        if (PASSTHRU_OPERATOR.contains(operator.asBig())|| operator.asString().startsWith("_")) {
            // just return
            return r
        }

        symbolTable.asFlatListOfLists().forEach {
            val symbol = it[0]
            val value = it[1]
            if (symbol.contentEquals("*".encodeToByteArray().toUByteArray())) {
                return r
            }
            if (symbol.contentEquals(asAtom)) {

                val newArgs = eval(
                    SExp to listOf(
                        "opt",
                        listOf(
                            "com",
                            quote(listOf("list") + prog.rest().toList()),
                            quote(macroLookup),
                            quote(symbolTable)
                        )
                    ), NodePath.TOP.shortPath
                )
                return SExp to listOf(APPLY_ATOM, value, listOf(CONS_ATOM, NodePath.LEFT.shortPath, newArgs))
            }
        }

        throw EvalError("Compile ${dissasemble(progC)} failed. Unknown operator: ${operator}" )
    }
}
