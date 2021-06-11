package bls

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import util.crypto.Sha256
import util.extensions.toBytes

@ExperimentalUnsignedTypes
object HdKeys {

    fun keyGen(seed: UByteArray): PrivateKey {
        val L = 48
        val salt = "BLS-SIG-KEYGEN-SALT-".encodeToByteArray().toUByteArray()
        val ok = Hkdf.extractExpand(L, seed + ubyteArrayOf(0u), salt,
            ubyteArrayOf(0.toUByte(), L.toUByte())
        )
        return PrivateKey(BigInteger.fromUByteArray(ok, Sign.POSITIVE).mod(defaultEc.n))
    }

    fun ikmToLamportSk(ikm: UByteArray, salt: UByteArray): UByteArray =
        Hkdf.extractExpand(32 * 255, ikm, salt, ubyteArrayOf())

    fun parentSkToLamportPk(parentSk: PrivateKey, index: Int): UByteArray {
        val salt = index.toBytes()
        val ikm = parentSk.toUByteArray()
        val notIkm = ikm.map { it xor (0xFFu).toUByte() }.toUByteArray()
        val lamport0 = ikmToLamportSk(ikm, salt)
        val lamport1 = ikmToLamportSk(notIkm, salt)

        var lamportPk = UByteArray(0)
        for (i in 0 until 255) {
            lamportPk += Sha256().digest(lamport0.slice(i * 32 until (i + 1) * 32).toUByteArray())
        }
        for (i in 0 until 255) {
            lamportPk += Sha256()
                .digest(lamport1.slice(i * 32 until  (i + 1) * 32).toUByteArray())
        }

        return Sha256().digest(lamportPk)
    }

    fun deriveChildSk(parentSk: PrivateKey, index: Int): PrivateKey {
        val lamportPk = parentSkToLamportPk(parentSk, index)
        return keyGen(lamportPk)
    }

    fun deriveChildSkUnhardened(parentSk: PrivateKey, index: Int): PrivateKey {
        val h = Sha256().digest(parentSk.getG1().toUByteArray() + index.toBytes())
        return PrivateKey.aggregate(listOf(PrivateKey.fromUByteArray(h), parentSk))
    }

    fun deriveChildG1Unhardened(parentPk: JacobianPoint, index: Int): JacobianPoint {
        val h = Sha256().digest(parentPk.toUByteArray() + index.toBytes())
        return parentPk + G1Generator() * PrivateKey.fromUByteArray(h).value
    }

    fun deriveChildG2Unhardened(parentPk: JacobianPoint, index: Int): JacobianPoint {
        val h = Sha256().digest(parentPk.toUByteArray() + index.toBytes())
        return parentPk + G2Generator() * PrivateKey.fromUByteArray(h).value
    }
}