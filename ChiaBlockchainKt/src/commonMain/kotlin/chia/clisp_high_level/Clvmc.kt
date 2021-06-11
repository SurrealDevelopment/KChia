package chia.clisp_high_level

import chia.clisp_low_level.assemble
import chia.clisp_low_level.elements.SExp
import chia.clisp_high_level.s2.S2Bindings
import chia.clisp_high_level.s2.S2Run

object Clvmc {

    private val defaultEvaluator = S2Run.buildRunProgForSearchPath(listOf("puzzles", "runtime"))

    // if no search paths we can just use a general purpose default
    fun compileFromText(program: String): SExp {
        val assembled = assemble(program)

        val inputSexp = SExp to Pair(assembled, SExp to listOf())
        val runProgram = defaultEvaluator.evaluate(S2Bindings.run, inputSexp)
        return runProgram.second
    }

    fun compileFromText(program: String, searchPaths: List<String> = listOf()): SExp {
        val assembled = assemble(program)

        val inputSexp = SExp to Pair(assembled, SExp to listOf())
        val runProgram = S2Run.runProgForSearchPath(S2Bindings.run, inputSexp, searchPaths)
        return runProgram.second
    }
}