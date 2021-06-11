package chia

import chia.clisp_high_level.Clvmc
import chia.clisp_low_level.dissasemble
import chia.clisp_low_level.elements.SExp
import kotlin.test.Test
import kotlin.test.assertEquals

class ClvmHighLevel {
    @Test
    fun testOptimizations() {
        val x = (Clvmc.compileFromText("(mod (AA BB) (+ AA BB 55))"))
        assertEquals("ff10ff02ff05ffff013780", x.hex)
        val x2 = Clvmc.compileFromText("(opt (com (q . ((add2 (f (f @))))) (r (opt (com (q . (list (defmacro add2 (X) (qq ( + 2 (unquote X)))))))))))")
        assertEquals("ffff10ffff0102ff048080", x2.hex)
        val x3 = Clvmc.compileFromText("(mod ARGS (defun add (n1 n2) (+ n1 n2)) (add 50 60))")
        assertEquals("ff02ffff01ff02ff02ffff04ff02ffff01ff32ff3c808080ffff04ffff01ff10ff05ff0b80ff018080",
            x3.hex)
    }

    @Test
    fun codeSampleCompile() {
        val sample = """
            (mod (password new_puzhash amount)
              (defconstant CREATE_COIN 51)

              (if (= (sha256 password) (q . 0x2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824))
                (list (list CREATE_COIN new_puzhash amount))
                (x)
              )
            )
        """.trimIndent()

        val x = Clvmc.compileFromText(sample)
        println(x)
    }
}