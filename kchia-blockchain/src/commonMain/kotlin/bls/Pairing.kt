package bls

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import util.extensions.fastPow

object Pairing {
    fun bigToBits(i: BigInteger): List<Int> {
        if (i < 1) return listOf(0)
        var x = i
        val bits = ArrayList<Int>()
        while (x != BigInteger.ZERO) {
            bits.add(x.mod(BigInteger.TWO).intValue(true))
            x /= 2
        }
        return bits.reversed()
    }

    /**
     * Creates an equation for a line tangent to R,
     *   and evaluates this at the point P. f(x) = y - sv - v.
     *   f(P).
     */
    fun doubleLineEval(R: AffinePoint, P: AffinePoint, ec: EC = defaultEc): Field {
        val R12 = untwist(R)

        val slope = (Fq(ec.q, 3) * (R12.x.pow(2)) + ec.a) / (Fq(ec.q, 2) * R12.y)
        val v = R12.y - slope * R12.x
        return P.y - P.x * slope - v
    }

    /**
     * Creates an equation for a line between R and Q,
     * and evaluates this at the point P. f(x) = y - sv - v.
     * f(P).
     */
    fun addLineEval(R: AffinePoint, Q: AffinePoint, P: AffinePoint): Fq12 {
        val r12 = untwist(R)
        val q12 = untwist(Q)

        // this is the case of a vertical line where denominator will be 0
        if (r12 == -q12) {
            return (P.x - r12.x) as Fq12
        }
        val slope = (q12.y - r12.y) / (q12.x - r12.x)
        val v = (q12.y * r12.x - r12.y * q12.x) / (r12.x - q12.x)

        return (P.y - P.x * slope - v) as Fq12
    }

    /**
     *  Performs a double and add algorithm for the ate pairing. This algorithm
     *  is taken from Craig Costello's "Pairing for Beginners".
     */
    fun millerLoop(T: BigInteger, P: AffinePoint, Q: AffinePoint, ec: EC = defaultEc): Fq12 {
        val Tbits = bigToBits(T)
        var R = Q
        var f = Fq12.one(ec.q)
        for (i in 1 until Tbits.size) {
            // compute sloped line lrr
            val lrr = doubleLineEval(R, P, ec)

            f = (f * f * lrr) as Fq12

            R = (R * Fq(ec.q, 2))

            if (Tbits[i] == 1) {
                // compute sloped line lrq
                val lrq = addLineEval(R, Q, P)
                f = (f * lrq)
                R += Q
            }
        }
        return f
    }

    // Performs a final exponentiation to map the result of the miller
    //    loop to a unique element of Fq12.
    fun finalExponentation(element: Fq12, ec: EC = defaultEc): Fq12 {
        return if (ec.k == BigInteger(12)) {

            var ans = element.pow(
                (ec.q.fastPow(4) - ec.q.fastPow(2) + 1) / ec.n
            )
            ans = ans.qiPower(2) * ans
            ans = ans.qiPower(6) / ans
            ans
        } else {
            element.pow((ec.q.fastPow(ec.k) - 1) / ec.n)
        }
    }

    fun atePairing(P: JacobianPoint, Q: JacobianPoint, ec: EC = defaultEc): Fq12 {
        val t = defaultEc.x + 1
        var T = t - 1
        if (T < 0) T = -T
        val element = millerLoop(T, P.toAffine(), Q.toAffine(), ec)

        return finalExponentation(element, ec)
    }


    suspend fun atePairingMulti(Ps: List<JacobianPoint>, Qs: List<JacobianPoint>, ec: EC = defaultEc): Fq12
    = withContext(Dispatchers.Default) {
        val t = defaultEc.x + 1
        var T = t - 1
        if (T < 0) T = -T

        val results = Ps.zip(Qs).map { async {
            millerLoop(T, it.first.toAffine(), it.second.toAffine(), ec)
        } }
            .map { it.await() }
            .fold(Fq12.one(ec.q)) { acc, it ->
                acc * it
            }

        return@withContext finalExponentation(results, ec)
    }
}