package chia.clisp_high_level.s2

import chia.clisp_low_level.assemble

object S2Bindings {
    val brun = assemble("(a 2 3)")
    val run = assemble("(a (opt (com 2)) 3)")
}