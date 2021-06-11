package chia

import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.ops.Operators
import chia.clisp_low_level.assemble
import chia.clisp_high_level.runtime.curry
import chia.clisp_low_level.dissasemble
import chia.clisp_high_level.runtime.uncurry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ProgramTests {

    private fun checkIdempotency(f: SExp, args: SExp): String {
        val curryResult = curry(f, args)

        val r = dissasemble(curryResult.second)

        println(dissasemble(f))
        val fPair = uncurry(curryResult.second)

        assertNotNull(fPair)

        assertEquals(dissasemble(fPair.first), dissasemble(f))
        assertEquals(dissasemble(fPair.second), dissasemble(args))
        return r

    }
    @Test fun curryTest() {
        val args = assemble("(200 30)")

        val f = assemble("(+ 2 5)")

        val plusOpCode = Operators.KEYWORD_TO_ATOM["+"]!!.intValue(true)

        val r = (checkIdempotency(f, args))

        assertEquals("(a (q $plusOpCode 2 5) (c (q . 200) (c (q . 30) 1)))", r)
    }

}