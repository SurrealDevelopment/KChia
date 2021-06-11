package chia.clisp_low_level.ops

import com.ionspin.kotlin.bignum.integer.BigInteger

internal object ShallowOps {

    val opQuote = buildOp(0x01,"q", "q") { args->
        throw IllegalStateException("Quote should not be handled this way")
    }

    val opApply = buildOp(0x02,"a", "a") { args->
        throw IllegalStateException("Apply should not be handled this way")
    }

    val list = listOf(opQuote, opApply)

}