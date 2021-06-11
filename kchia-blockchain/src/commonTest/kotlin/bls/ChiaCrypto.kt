package bls

import util.hexstring.toHexString
import util.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalUnsignedTypes
class ChiaCrypto {
    @Test fun testChiaVectors1() = runBlockingTest {
        val seed1 = UByteArray(32) { 0u }
        val seed2 = UByteArray(32){ 1u }
        val msg1 = byteArrayOf(7, 8, 9)
        val msg2 = byteArrayOf(10, 11, 12)

        val sk1 = BasicSchemeMPL.keyGen(seed1)
        val sk2 = BasicSchemeMPL.keyGen(seed2)
        assertEquals(sk1.toUByteArray().toHexString(),
            "4a353be3dac091a0a7e640620372f5e1e2e4401717c1e79cac6ffba8f6905604")
        assertEquals(sk1.getG1().toByteArray().toHexString(),
        "85695fcbc06cc4c4c9451f4dce21cbf8de3e5a13bf48f44cdbb18e2038ba7b8bb1632d7911ef1e2e08749bddbf165352")

        val sig1 = BasicSchemeMPL.sign(sk1, msg1)
        val sig2 = BasicSchemeMPL.sign(sk2, msg2)

        assertEquals("b8faa6d6a3881c9fdbad803b170d70ca5cbf1e6ba5a586262df368c75acd1d1ffa3ab6ee21c71f8" +
                "44494659878f5eb230c958dd576b08b8564aad2ee0992e85a1e565f299cd53a285de729937f70dc176a1f01432129" +
                "bb2b94d3d5031f8065a1",
            sig1.toByteArray().toHexString()
        )

        assertEquals("a9c4d3e689b82c7ec7e838dac2380cb014f9a08f6cd6ba044c263746e39a8f7a60ffee4afb7" +
            "8f146c2e421360784d58f0029491e3bd8ab84f0011d258471ba4e87059de295d9aba845c044e" +
            "e83f6cf2411efd379ef38bf4cf41d5f3c0ae1205d",
            sig2.toByteArray().toHexString()
        )

        val appSig = BasicSchemeMPL.aggregate(sig1, sig2)

        assertEquals("aee003c8cdaf3531b6b0ca354031b0819f7586b5846796615aee8108fec75ef838d181f9d24" +
            "4a94d195d7b0231d4afcf06f27f0cc4d3c72162545c240de7d5034a7ef3a2a03c0159de982fb" +
            "c2e7790aeb455e27beae91d64e077c70b5506dea3",
            appSig.toByteArray().toHexString()
            )
        assertTrue {
            BasicSchemeMPL.aggregateVerify(listOf(sk1.getG1(), sk2.getG1()), listOf(msg1, msg2),
                appSig)
        }

        val msg3 = byteArrayOf(1, 2, 3)
        val msg4 = byteArrayOf(1, 2, 3, 4)
        val msg5 = byteArrayOf(1, 2)

        val sig3 = BasicSchemeMPL.sign(sk1, msg3)
        val sig4 = BasicSchemeMPL.sign(sk1, msg4)
        val sig5 = BasicSchemeMPL.sign(sk2, msg5)

        val appSig2 = BasicSchemeMPL.aggregate(sig3, sig4, sig5)

        assertTrue {
            BasicSchemeMPL.aggregateVerify(listOf(sk1.getG1(), sk1.getG1(), sk2.getG1()),
                listOf(msg3, msg4, msg5), appSig2)
        }
        assertEquals("a0b1378d518bea4d1100adbc7bdbc4ff64f2c219ed6395cd36fe5d2aa44a4b8e710b607afd9" +
            "65e505a5ac3283291b75413d09478ab4b5cfbafbeea366de2d0c0bcf61deddaa521f6020460f" +
            "d547ab37659ae207968b545727beba0a3c5572b9c", appSig2.toByteArray().toHexString())
    }
}