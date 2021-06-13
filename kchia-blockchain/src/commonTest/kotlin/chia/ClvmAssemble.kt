@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia

import chia.clisp_low_level.assemble
import chia.clisp_low_level.ir.TokenReader
import chia.clisp_low_level.ir.readIr
import chia.clisp_low_level.ToSexpFun
import chia.clisp_low_level.elements.SExp
import chia.clisp_low_level.elements.toImp
import chia.clisp_low_level.ops.Operators
import chia.clisp_low_level.runProgram
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import util.hexstring.toHexString
import kotlin.test.*

class ClvmAssemble {
    val curry = "(a (q #a 4 (c 2 (c 5 (c 7 0)))) (c (q (c (q . 2) (c (c (q . 1) 5) (c (a 6 (c 2 (c 11 (q 1)))) 0))) #a (i 5 (q 4 (q . 4) (c (c (q . 1) 9) (c (a 6 (c 2 (c 13 (c 11 0)))) 0))) (q . 11)) 1) 1))"


    val defaultToSExp: ToSexpFun = { SExp.toImp(it) }
    @Test
    fun testTokenStream() {
        assertNotNull(TokenReader(curry, defaultToSExp))
    }

    @Test
    fun noDuplicateOps() {
        // insure no clvm opcodes are accidentally duplicated which would cause undefined behavior.
        Operators.keyOps.forEach { op1 ->
            Operators.keyOps.minus(op1).forEach { op2 ->
                assertNotEquals(op1.opCode!!, op2.opCode)
                assertNotEquals(op1.opName, op2.opName)
                assertNotEquals(op1.rewriteSymbol, op2.rewriteSymbol)
            }
        }
    }

    @Test
    fun testIr() {
        assertEquals("ffff84434f4e5301ffffff83494e540164ffff84434f4e5305ffffff8348455805820100ffff844e554c4c0b80",
            readIr("(100 0x0100)").hex)
        assertEquals("ffff8348455880820100", readIr("0x0100").hex )
        assertEquals(readIr("0x100").hex, readIr("0x0100").hex )
        assertEquals("ffff834451548083313030", readIr("\"100\"").hex )

        assertEquals("ffff8353594d8083666f6f", readIr("foo").hex)
        assertEquals("ffff84434f4e5301ffffff8353594d0163ffff84434f4e5303ffffff84434f4e5304ffffff8353594d048571756f7465ffff84434f4e530affffff83494e540a64ffff844e554c4c0d80ffff84434f4e530fffffff84434f4e5310ffffff8353594d1063ffff84434f4e5312ffffff84434f4e5313ffffff8353594d138571756f7465ffff84434f4e5319ffffff834451541983666f6fffff844e554c4c1e80ffff84434f4e5320ffffff84434f4e5321ffffff8353594d218571756f7465ffff84434f4e5327ffffff844e554c4c2880ffff844e554c4c2980ffff844e554c4c2a80ffff844e554c4c2b80",
            readIr("(c (quote 100) (c (quote \"foo\") (quote ())))").hex)
        assertEquals("ffff84434f4e5301ffffff8353594d0163ffff8353594d0583666f6f",
            readIr("(c . foo)").hex)
        assertEquals("ffff84434f4e5301ffffff8353594d012bffff84434f4e5303ffffff83494e540302ffff84434f4e5305ffffff83494e540503ffff844e554c4c0680",
            readIr("(+ 2 3)").hex)
    }

    @Test
    fun testIrComplicated() {
        assertEquals("ffff84434f4e5301ffffff8353594d0161ffff84434f4e5303ffffff84434f4e5304ffffff8353594d0471ffff84434f4e5309ffffff8353594d093affff8353594d0d8866756e6374696f6effff84434f4e5318ffffff84434f4e5319ffffff8353594d193affff8353594d1d84636f7265ffff844e554c4c2280",
            readIr("(a (q . (: . function)) (: . core))").hex)
    }

    @Test
    fun testArithmetics() {
        // test various simple math

        // this shall just echo 100
        val show = assemble("(q . 100)")
        val run = runProgram(show, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals(BigInteger(20), run.first)
        assertEquals(BigInteger(100), BigInteger.fromUByteArray(run.second.atom!!, Sign.POSITIVE))

        val add = assemble("(+ (q . 10) (q . 20) (q . 30) (q . 40))")
        val run2 = runProgram(add, SExp.__null__, Operators.OPERATOR_LOOKUP)

        assertEquals(BigInteger(100), run2.second.asBig())
    }

    @Test
    fun testSigned() {

        // negative result
        val subAssm = assemble("(- (q . 5) (q . 7))")
        val run3 = runProgram(subAssm, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals(BigInteger(-2), run3.second.asBig())

        // negative number
        val subAssm2 = assemble("(+ (q . 3) (q . -8))")
        val run2 = runProgram(subAssm2, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals(BigInteger(-5), run2.second.asBig())
    }

    @Test
    fun testStrOps() {

        val substr = assemble("(substr (q . \"clvm\") (q . 0) (q . 4))")
        val run3 = runProgram(substr, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals("clvm", run3.second.asString())

        val substr2 = assemble("(substr (q . \"clvm\") (q . 2) (q . 4))")
        val run2 = runProgram(substr2, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals("vm", run2.second.asString())

        val strlen = assemble("(strlen (q . \"clvm\"))")
        val run4 = runProgram(strlen, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals(BigInteger(4), run4.second.asBig())
    }

    @Test
    fun testBitwiseOperations() {
        val asm = assemble("(lognot (q . ()))")
        val run = runProgram(asm, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals(BigInteger(-1),
            run.second.asBig())

        val asm2 = assemble("(lognot (q . 1))")
        val run2 = runProgram(asm2, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals(BigInteger(-2),
            run2.second.asBig())
        val asm3 = assemble("(concat (q . -2) (q . -2))")
        val run3 = runProgram(asm3, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals("fefe",
            run3.second.atom!!.toHexString())
        val asm4 = assemble("(concat (q . -2) (q . -2) (q . -2) (q . -2))")
        val run4 = runProgram(asm4, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals("fefefefe",
            run4.second.atom!!.toHexString())

        val asm5 = assemble("(logxor (q . 0x01) (q . 0x01ff))")
        val run5 = runProgram(asm5, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals("01fe",
            run5.second.atom!!.toHexString())
        val asm6 = assemble("(logand (q . 0x01) (q . 0x03))")
        val run6 = runProgram(asm6, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals("01",
            run6.second.atom!!.toHexString())
    }

    @Test
    fun testSha256Op() {

        val asm = assemble("(sha256 (q . \"clvm\"))")
        val run = runProgram(asm, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals("cf3eafb281c0e0e49e19c18b06939a6f7f128595289b08f60c68cef7c0e00b81",
            run.second.atom!!.toHexString())
    }

    @Test
    fun testBlsOp() {

        val asm = assemble("(strlen (pubkey_for_exp (q . 1)))")
        val run = runProgram(asm, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals(BigInteger(48),
            run.second.asBig())
        val asm3 = assemble("(pubkey_for_exp (q . 1))")
        val run3 = runProgram(asm3, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals("97f1d3a73197d7942695638c4fa9ac0fc3688c4f9774b905a14e3a3f171bac586c55e83ff97a1aeffb3af00adb22c6bb",
            run3.second.atom!!.toHexString())

        val asm2 = assemble("(point_add (pubkey_for_exp (q . 1)) (pubkey_for_exp (q . 2)))")
        val run2 = runProgram(asm2, SExp.__null__, Operators.OPERATOR_LOOKUP)
        assertEquals("89ece308f9d1f0131765212deca99697b112d61f9be9a5f1f3780a51335b3ff981747a0b2ca2179b96d2c0c9024e5224",
            run2.second.atom!!.toHexString())

    }


    @Test
    fun testMiscCases() {
        assertEquals("ff846c697374ff32ff3c80", assemble("(\"list\" 50 60)").hex)
    }


}