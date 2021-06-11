@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia


import chia.clisp_low_level.elements.SExp
import util.hexstring.toHexString
import util.hexstring.uByteArrayFrom
import kotlin.test.Test
import kotlin.test.assertEquals

class SexpSerializationTests {


    @Test
    fun thisToThatOps() {
        assertEquals((SExp to 1).hex, "01")
        assertEquals((SExp to Pair(1, 2)).hex, "ff0102")

        val a = uByteArrayFrom("9dcf97a184f32623d11a73124ceb99a5709b083721e878a16d78f596718ba7b2")
        val b = uByteArrayFrom("2ea76a9220b2aa44f86a574455bf9a50f6bb48e0c9dbc8a70dc892607350123b")

        val tuple = SExp to listOf(a, b)

        assertEquals(
            tuple.hex,
            "ffa09dcf97a184f32623d11a73124ceb99a5709b083721e878a16d78f596718ba7b2ffa02ea76a9220b2aa" +
                    "44f86a574455bf9a50f6bb48e0c9dbc8a70dc892607350123b80"
        )

    }

    @Test
    fun treeHash() {
        val sexp = SExp to "test_hash"
        assertEquals("89746573745f68617368", sexp.hex)

        assertEquals(
            "2ea76a9220b2aa44f86a574455bf9a50f6bb48e0c9dbc8a70dc892607350123b",
            sexp.treeHash.toHexString()
        )

    }

}