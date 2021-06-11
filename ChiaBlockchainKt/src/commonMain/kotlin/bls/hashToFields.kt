package bls

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import util.crypto.HashFunction
import util.crypto.Sha256
import kotlin.experimental.xor


typealias ExpandFunction = (msg: ByteArray, DST: ByteArray, lenInBytes: Int, hashFn: HashFunction) -> ByteArray

object hashToFields {

    fun I2OSP(value: Int, length: Int): ByteArray = I2OSP(BigInteger(value), length)

    /// RFC 3447 4.1
    fun I2OSP(value: BigInteger, length: Int): ByteArray {
        if (value < 0 || value >= (BigInteger.ONE shl length * 8)) {
            throw Exception("Bad I2SO call: val=$value, lenggth=$length")
        }
        val array = ByteArray(length) {0}
        var num = value
        for (i in (length-1) downTo 0) {
            array[i] = num.and(BigInteger(0xff)).intValue(true).toByte()
            num = num shr 8
        }
        return array
    }

    fun O2SIP(octets: ByteArray): BigInteger {
        return BigInteger.fromByteArray(octets, Sign.POSITIVE)
    }

    fun strxor(str1: ByteArray, str2: ByteArray): ByteArray {
        return str1.zip(str2).map { it.first.xor(it.second) }.toByteArray()
    }

    val expandMessageXmd: ExpandFunction = { msg, dst, len, hashFunction ->
        val bInBytes = hashFunction.digestSize
        val rInBytes = hashFunction.blockSize


        // ell DST Prime, etc
        val ell = (len + bInBytes - 1) / bInBytes
        if (ell > 255) throw IllegalArgumentException("$ell out of range")



        val dstPrime = dst + I2OSP(BigInteger(dst.size), 1)
        val zPad = I2OSP(BigInteger.ZERO, rInBytes)
        val libStr = I2OSP(BigInteger(len), 2)



        val b0 = hashFunction.digest(zPad + msg + libStr + I2OSP(BigInteger.ZERO, 1) + dstPrime)

        val bvals = MutableList<ByteArray>(ell) { ByteArray(0) }
        bvals[0] = hashFunction.digest(b0 + I2OSP(1, 1) + dstPrime)
        for (i in 1 until ell) {
            bvals[i] = hashFunction.digest(
                strxor(b0, bvals[i - 1]) + I2OSP(i + 1, 1) + dstPrime
            )
        }
        val psuedoRandomBytes = bvals.reduce { acc, bytes -> acc + bytes }
        psuedoRandomBytes.take(len).toByteArray()
    }

    val expandMessageXof: ExpandFunction = { msg, dst, len, hashFunction ->
        val DSTprime = dst + I2OSP(dst.size, 1)
        val msgPrime = msg + I2OSP(len, 2) + DSTprime
        hashFunction.digest(msgPrime)
    }


    fun hashToField(
        msg: ByteArray, count: Int, dst: ByteArray, modulus: BigInteger, degree: Int, blen: Int,
        expandFn: ExpandFunction, hashFn: HashFunction
    ): List<List<BigInteger>> {
        val lenInBytes = count * degree * blen
        val psuedoRandomBytes = expandFn(msg, dst, lenInBytes, hashFn)


        val uvals = MutableList<List<BigInteger>>(count) { listOf() }


        for (i in 0 until count) {
            val evals = MutableList(degree) { BigInteger.ZERO }
            for (j in 0 until degree) {
                val elmOffset = blen * (j + i * degree)
                val tv = psuedoRandomBytes.slice(elmOffset until elmOffset + blen)
                evals[j] = O2SIP(tv.toByteArray()).mod(modulus)
            }
            uvals[i] = evals
        }
        return uvals
    }


    fun Hp(msg: ByteArray, count: Int, dst: ByteArray): List<List<BigInteger>> {
        return hashToField(msg, count, dst, Bls12381.q, 1, 64, expandMessageXmd, Sha256())
    }


    fun Hp2(msg: ByteArray, count: Int, dst: ByteArray): List<List<BigInteger>> {
        return hashToField(msg, count, dst, Bls12381.q, 2, 64, expandMessageXmd, Sha256())
    }

}
