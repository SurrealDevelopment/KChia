package bls

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import util.runBlockingTest
import util.hexstring.HexString
import util.hexstring.asHexString
import kotlin.test.*

@ExperimentalUnsignedTypes
class TestBls {

    @Test
    fun testHkdf() {
        fun testCase(
            ikmHex: HexString, saltHex: HexString, infoHex: HexString,
            prkExpectedHex: HexString, okmExpected: HexString, L: Int
        ) {
            val prk = Hkdf.extract(saltHex.toUByteArray(), ikmHex.toUByteArray())
            val okm = Hkdf.expand(L, prk, infoHex.toUByteArray())
            assertEquals(32, prkExpectedHex.toUByteArray().size)
            assertEquals(L, okmExpected.toUByteArray().size)
            assertTrue { prk.contentEquals(prkExpectedHex.toUByteArray()) }
            assertTrue { okm.contentEquals(okmExpected.toUByteArray()) }
        }
        testCase(
            "0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b".asHexString(),
            "000102030405060708090a0b0c".asHexString(),
            "f0f1f2f3f4f5f6f7f8f9".asHexString(),
            "077709362c2e32df0ddc3f0dc47bba6390b6c73bb50f9c3122ec844ad7c2b3e5".asHexString(),
            "3cb25f25faacd57a90434f64d0362f2a2d2d0a90cf1a5a4c5db02d56ecc4c5bf34007208d5b887185865".asHexString(),
            42
        )
    }

    @Test
    fun testFields() {
        val a = Fq(17, 30)

        val b = Fq(17, -18)
        val c = Fq2(BigInteger(17), a, b)
        val d = Fq2(BigInteger(17), a + a, Fq(17, -5))
        val e = c * d
        val f = e * d
        assertNotEquals(f, e)
        val eSq = e * e


        val eSqrt = eSq.modSqrt()
        assertEquals(eSqrt.pow(2), eSq)


    }

    @Test
    fun testEcMath() {
        val q = defaultEc.q
        val g = G1Generator()
        assertTrue(g.isOnCurve())
        assertEquals(g * BigInteger.TWO, g + g)

        assertTrue((g * BigInteger(3)).isOnCurve())
        assertEquals(g * BigInteger(3), g + g + g)

        val g2 = G2Generator()
        assertEquals(g2.x * (Fq(q, 2) * g2.y), Fq(q, 2) * (g2.x * g2.y))
        assertTrue(g2.isOnCurve())

        val s = g2 + g2
        val l1 = untwist(twist(s.toAffine()))
        val l2 = s.toAffine()
        assertEquals(l1, l2)
        assertEquals(untwist(twist(s.toAffine()) * 5), (s * 5).toAffine())
        assertEquals(twist(s.toAffine()) * 5, twist((s * 5).toAffine()))
        assertTrue(s.isOnCurve())
        assertTrue(g2.isOnCurve())
        assertEquals(g2 + g2, g2 * 2)


        val y = yForX(g2.x, defaultEcTwist, Fq2)
        assertTrue(y == g2.y || -y == g2.y)

        val gJ = G1Generator()
        val g2J = G2Generator()
        val g2J2 = G2Generator() * 2

        assertEquals(g.toAffine().toJacobian(), g)
        assertEquals((gJ * 2).toAffine(), g.toAffine() * 2)
        assertEquals((g2J + g2J2).toAffine(), g2.toAffine() * 3)

    }

    @Test
    fun testElements()  {

        val i1 = BigInteger.fromByteArray(byteArrayOf(1, 2), Sign.POSITIVE)
        val i2 = BigInteger.fromByteArray(byteArrayOf(3, 1, 4, 1, 5, 9), Sign.POSITIVE)
        val b1 = i1
        val b2 = i2
        val g1 = G1Generator()
        val g2 = G2Generator()
        val u1 = G1Infinity()
        val u2 = G2Infinity()

        var x1 = g1 * b1
        val x2 = g1 * b2
        var y1 = g2 * b1
        val y2 = g2 * b2

        // g1
        assertNotEquals(x1, x2)
        assertEquals(x1 * b1, x1 * b1)
        assertNotEquals(x1 * b1, x1 * b2)

        val left = x1 + u1
        val right = x1

        assertEquals(left, right)
        assertEquals(x1 + x2, x2 + x1)
        assertEquals(x1, G1FromBytes(x1.toByteArray()))
        x1 += x2


        // G2
        assertNotEquals(y1, y2)
        // assertEquals(y1 * b1, b1 * y1) not supported atm
        assertNotEquals(y1 * b1, y1 * b2)
        assertEquals(y1 + u2, y1)
        assertEquals(y1 + y2, y2 + y1)
        assertEquals(y1 + -y1, u2)
        assertEquals(y1, G2FromBytes(y1.toByteArray()))
        y1 += y2
        // Pairing operations
        val pair = Pairing.atePairing(x1, y1)
        assertNotEquals(pair, Pairing.atePairing(x1, y2))
        assertNotEquals(pair, Pairing.atePairing(x2, y1))

        val sk = BigInteger.parseString("728934712938472938472398074")
        val pk = g1 * sk
        val Hm = y2 * BigInteger.parseString("12371928312") + y2 * BigInteger.parseString("12903812903891023")

        val sig = Hm * sk

        assertEquals(Pairing.atePairing(g1, sig), Pairing.atePairing(pk, Hm))
    }

    @Test fun testInvalidVectors() = runBlockingTest {
        val invalid_inputs_1 = listOf(
            // infinity points: too short
            "c000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".asHexString(),
            // infinity points: not all zeros
                "c00000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000".asHexString(),
            // bad tags
                "3a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaaa".asHexString(),
            "7a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaaa".asHexString(),
            "fa0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaaa".asHexString(),
            // wrong length for compresed point
                "9a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaa".asHexString(),
            "9a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaaaaa".asHexString(),
            // invalid x-coord
            "9a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaaa".asHexString(),
            // invalid elm of Fp --- equal to p (must be strictly less)
            "9a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaab".asHexString(),
        )
        val invalid_inputs_2 = listOf(
            // infinity points: too short
            "c000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".asHexString(),
            // infinity points: not all zeros
                "c00000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".asHexString(),
            "c00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000".asHexString(),
            // bad tags
                "3a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaaa000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".asHexString(),
            "7a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaaa000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".asHexString(),
            "fa0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaaa000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".asHexString(),
            // wrong length for compressed point
                "9a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaaa0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".asHexString(),
            "9a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaaa00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".asHexString(),
            // invalid x-coord
            "9a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaaa1a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaa7".asHexString(),
            // invalid elm of Fp --- equal to p (must be strictly less)
            "9a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaab000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000".asHexString(),
            "9a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaaa1a0111ea397fe69a4b1ba7b6434bacd764774b84f38512bf6730d2a0f6b0f6241eabfffeb153ffffb9feffffffffaaab".asHexString(),
        )

        invalid_inputs_1.forEachIndexed { index, it ->
            val bytes = it.toByteArray()
            assertFailsWith(Exception::class, "Failed to disallow creation of G1 element. Case $index\n$it") {
                G1FromBytes(bytes).checkValid()
            }
        }

        invalid_inputs_2.forEachIndexed { index, it ->
            val bytes = it.toByteArray()
            assertFailsWith(Exception::class, "Failed to disallow creation of G2 element. Case $index\n$it") {
                G2FromBytes(bytes).checkValid()
            }
        }
    }

    @Test fun testReadMe() = runBlockingTest {
        var seed = listOf(0,
            50,
            6,
            244,
            24,
            199,
            1,
            25,
            52,
            88,
            192,
            19,
            18,
            12,
            89,
            6,
            220,
            18,
            102,
            58,
            209,
            82,
            12,
            62,
            89,
            110,
            182,
            9,
            44,
            20,
            254,
            22,).toTypedArray().map { it.toUByte() }.toUByteArray()

        var sk = AugSchemeMPL.keyGen(seed)
        var pk: G1Element = sk.getG1()
        val message = byteArrayOf(1, 2, 3, 4, 5)
        var signature: G2Element = AugSchemeMPL.sign(sk, message).asG2()

        val verify = AugSchemeMPL.verify(pk, message, signature)
        assertTrue(verify)

        // test encode decode
        val skBytes = sk.toUByteArray()
        val pkBytes = pk.toByteArray()
        val signatureBytes = signature.toByteArray()

        sk = PrivateKey.fromUByteArray(skBytes)
        assertNotNull(sk)
        pk = G1FromBytes(pkBytes)
        assertNotNull(pk)
        signature = G2FromBytes(signatureBytes)

        seed = ubyteArrayOf(1u) + seed.drop(1)
        val sk1 = AugSchemeMPL.keyGen(seed)
        seed = ubyteArrayOf(2u) + seed.drop(1)
        val sk2 = AugSchemeMPL.keyGen(seed)
        val message2 = byteArrayOf(1, 2, 3, 4, 5, 6, 7)

        val pk1 = sk1.getG1()
        val sig1 = AugSchemeMPL.sign(sk1, message)
        val pk2 = sk2.getG1()
        val sig2 = AugSchemeMPL.sign(sk2, message2)

        val appSig = AugSchemeMPL.aggregate(listOf(sig1, sig2))

        assertTrue { AugSchemeMPL.aggregateVerify(listOf(pk1, pk2), listOf(message, message2), appSig) }

        seed = ubyteArrayOf(3u) + seed.drop(1)
        val sk3 = AugSchemeMPL.keyGen(seed)
        val pk3 = sk3.getG1()
        val message3 = ubyteArrayOf(100u, 2u, 254u, 88u, 90u, 45u, 23u).toByteArray()
        val sig3 = AugSchemeMPL.sign(sk3, message3)

        val appSigFinal = AugSchemeMPL.aggregate(listOf(appSig, sig3))
        assertTrue {
            AugSchemeMPL.aggregateVerify(listOf(pk1, pk2, pk3), listOf(message, message2, message3),
                appSigFinal)
        }

        // pop scheme
        val popSig1: G2Element = PopSchemeMPL.sign(sk1, message).asG2()
        val popSig2: G2Element = PopSchemeMPL.sign(sk2, message).asG2()
        val popSig3: G2Element = PopSchemeMPL.sign(sk3, message).asG2()
        val pop1: G2Element = PopSchemeMPL.popProve(sk1).asG2()
        val pop2: G2Element = PopSchemeMPL.popProve(sk2).asG2()
        val pop3: G2Element = PopSchemeMPL.popProve(sk3).asG2()

        assertTrue(PopSchemeMPL.popVerify(pk1, pop1))
        assertTrue(PopSchemeMPL.popVerify(pk2, pop2))
        assertTrue(PopSchemeMPL.popVerify(pk3, pop3))

        val popSigAgg: G2Element = PopSchemeMPL.aggregate(listOf(popSig1, popSig2, popSig3)).asG2()

        assertTrue(PopSchemeMPL.fastAggregateVerify(listOf(pk1, pk2, pk3), message, popSigAgg))

        val popAggPk = pk1 + pk2 + pk3
        assertTrue(PopSchemeMPL.verify(popAggPk, message, popSigAgg))

        val popAggSk = PrivateKey.aggregate(listOf(sk1, sk2, sk3))
        assertEquals(PopSchemeMPL.sign(popAggSk, message), popSigAgg)

        val masterSk = AugSchemeMPL.keyGen(seed)
        val child = AugSchemeMPL.deriveChildSk(masterSk, 152)
        val grandchild = AugSchemeMPL.deriveChildSk(child, 952)

        val masterPk = masterSk.getG1()
        val childU: PrivateKey =  AugSchemeMPL.deriveChildSkUnderdended(masterSk, 22)
        val grandChildU: PrivateKey = AugSchemeMPL.deriveChildSkUnderdended(childU, 0)

        val childUPk: G1Element = AugSchemeMPL.deriveChildPkUnhardened(masterPk, 22).asG1()
        val grandchildUPk: G1Element = AugSchemeMPL.deriveChildPkUnhardened(childUPk, 0).asG1()

        assertEquals(grandchildUPk, grandChildU.getG1())


    }


}