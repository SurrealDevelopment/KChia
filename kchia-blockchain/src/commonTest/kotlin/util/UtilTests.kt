package util

import com.ionspin.kotlin.bignum.integer.BigInteger
import util.crypto.HmacSha256
import util.crypto.sha256
import util.extensions.*
import util.hexstring.HexString
import util.hexstring.asHexString
import util.hexstring.toHexString
import kotlin.test.*

@ExperimentalUnsignedTypes
class UtilTests {
    @Test
    fun uIntToByteAndString() {
        val uint = 0x1234u
        val format = uint.toString(16)
        assertEquals(format, uint.toBytes().trimSigned().toHexString())
    }

    @Test
    fun intToByteAndString() {
        val uint = 0x1234
        val format = uint.toString(16)
        assertEquals(format, uint.toBytes().trimSigned().toHexString())
    }

    @Test
    fun uLongToByteAndString() {
        val uint = 0x12345678uL
        val format = uint.toString(16)
        assertEquals(format, uint.toBytes().trimSigned().toHexString())
    }


    @Test
    fun detect0xWorks() {
        assertEquals(HexString("2").has0xPrefix(), false)
        assertEquals(HexString("0xFF").has0xPrefix(), true)
    }




    @Test
    fun hexToByteArrayWorks() {
        assertTrue(HexString("").toByteArray().isEmpty())
        assertEquals(HexString("02").toByteArray()[0], 2.toByte())
        assertEquals(HexString("0xFF").toByteArray()[0], (0xff).toByte())
        assertTrue(HexString("0xFFaa").toByteArray().toHexString() == "ffaa")
    }


    @Test
    fun detectingValidHexWorks() {
        assertTrue(HexString("0x").isValidHex())
        assertTrue(HexString("0x1a").isValidHex())
        assertTrue(HexString("0x1abcdef").isValidHex())
        assertTrue(HexString("").isValidHex())
        assertTrue(HexString("1a").isValidHex())
        assertTrue(HexString("1abcdef").isValidHex())
    }

    @Test
    fun detectingInvalidHexWorks() {
        assertFalse(HexString("0x0x").isValidHex())
        assertFalse(HexString("gg").isValidHex())
        assertFalse(HexString("ab0xcd").isValidHex())
        assertFalse(HexString("yolo").isValidHex())
        assertFalse(HexString("0xyolo").isValidHex())
    }

    @Test
    fun bitCount() {
        assertEquals(2, 2.bitCount())
        assertEquals(2, 3.bitCount())
        assertEquals(3, 4.bitCount())
        assertEquals(1, 4.bytesRequired())
        assertEquals(0, 0.bytesRequired())
        assertEquals(2, (0x100).bytesRequired())
        assertEquals(2, (0xffff).bytesRequired())
        assertEquals(3, (0xffffff).bytesRequired())
        assertEquals(4, (0x7fffffff).bytesRequired())

    }

    @Test
    fun toBytes() {
        assertTrue(ubyteArrayOf(0u).contentEquals(0.toBytes(1)))
        assertTrue(ubyteArrayOf(1u).contentEquals(1.toBytes(1)))

        // edge case clvm behavior to insure numbers dont sign flip on us
        assertContentEquals(ubyteArrayOf(0u, 0xffu), BigInteger(0xff).toTrimmed2sCompUbyteARray())

        assertContentEquals(ubyteArrayOf(206u), BigInteger(-50).toTrimmed2sCompUbyteARray())
        assertContentEquals(ubyteArrayOf(0x80u), BigInteger(-0x80).toTrimmed2sCompUbyteARray())
        assertContentEquals(ubyteArrayOf(0xffu, 0x7fu), BigInteger(-0x81).toTrimmed2sCompUbyteARray())

    }

    @Test
    fun sha256() {
        assertEquals("3a7bd3e2360a3d29eea436fcfb7e44c735d117c42d1c1835420b6b9942dd4f1b",
            "apple".encodeToByteArray().sha256().toHexString()
        )
    }


    @Test
    fun hmac() {
        assertEquals("001452663ce6aff95993561a08dc286ecf9634c8fb126bfe8c9aab971c9baef5",
            HmacSha256.hmac("apple".encodeToByteArray(),
                "Hello World".encodeToByteArray()
            ).toHexString()
        )
    }


}