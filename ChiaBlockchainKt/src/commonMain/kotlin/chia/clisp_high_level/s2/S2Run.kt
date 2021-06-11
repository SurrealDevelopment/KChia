package chia.clisp_high_level.s2

import chia.clisp_low_level.*
import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.elements.*
import chia.clisp_low_level.ops.EvalError
import chia.clisp_low_level.ops.Operators
import chia.clisp_low_level.ops.buildOpByName
import chia.clisp_low_level.runProgram
import com.ionspin.kotlin.bignum.integer.BigInteger
import util.file.isFile
import util.file.readBytesOfResource

object S2Run {


    /**
     * Use this when calling multiple times as it can be expensive to remake macros, etc...
     */
    fun buildRunProgForSearchPath(paths: List<String> = listOf()): Evaluator {
        val newLookup = Operators.OperatorMap.from(Operators.OPERATOR_LOOKUP)

        val runProg0: Evaluator = object : Evaluator() {
            override fun evaluate(program: SExp, args: SExp, maxCost: BigInteger?): Pair<BigInteger, SExp> {
                return runProgram(program, args, newLookup, maxCost)
            }
        }
        val macroDefaults = MacroDefaults(runProg0)

        val bindings = listOf(
            buildOpByName("com") { sexp ->
                val prog = sexp.first()
                var symTable = SExp.__null__
                val macroLookup =
                    if (!sexp.rest().nullp()) {
                        if (!sexp.rest().rest().nullp())
                            symTable = sexp.rest().rest().first()
                        sexp.rest().first()
                    } else {
                        macroDefaults.defaultMacroLookup()
                    }

                Pair(BigInteger.ONE, S2Compile.doComProg(prog, macroLookup, symTable, runProg0))
            },
            buildOpByName("opt") { sexp ->
                Pair(BigInteger.ONE, S2Optimize.optimizeSexp(sexp.first(), runProg0))
            },
            buildOpByName("_full_path_for_name") { sexp ->
                val filename = sexp.first().asString()
                val test = paths.find { path ->
                    val fPath = "$path/$filename"
                    isFile(fPath)
                } ?: throw EvalError("Cannot find or open $filename")
                Pair(BigInteger.ONE, SExp to "$test/$filename")
            },
            buildOpByName("_read") { sexp ->
                val f = sexp.first().asString()
                val bytes = readBytesOfResource(f)
                val asm = assemble(bytes.decodeToString())
                Pair(BigInteger.ONE, asm)
            },
            buildOpByName("_write") { sexp ->
                TODO("Write not yet implemented for KtCLVM")
            }
        ).map {
            Pair(it.opCode!!, it)
        }

        newLookup.map.putAll(bindings)

        return object : Evaluator() {
            override fun evaluate(program: SExp, args: SExp, maxCost: BigInteger?): Pair<BigInteger, SExp> {
                return runProgram(program, args, newLookup, maxCost)
            }
        }

    }


    @Deprecated("Build runner manually", ReplaceWith(
        "buildRunProgForSearchPath(paths)"
    )
    )
    fun runProgForSearchPath(program: SExp,
                             args: SExp,
                             paths: List<String> = listOf(),
                             maxCost: BigInteger? = null, // infinite
                             preEvalOpFun: PreEvalCallable? = null,
                             strict: Boolean = false
    ): Pair<BigInteger, SExp> {

        return buildRunProgForSearchPath(paths).evaluate(program, args, null)

    }
}

