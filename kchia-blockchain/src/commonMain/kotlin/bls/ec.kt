package bls

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import util.crypto.Sha256
import util.hexstring.toHexString
import kotlin.experimental.or

data class EC(
    val q: BigInteger,
    val a: Field,
    val b: Field,
    val gx: Fq,
    val gy: Fq,
    val g2x: Fq2,
    val g2y: Fq2,
    val n: BigInteger,
    val h: BigInteger,
    val x: BigInteger,
    val k: BigInteger,
    val sqrt_n3: BigInteger,
    val sqrt_n3m1o2: BigInteger
)

val defaultEc
    get() =
        EC(
            Bls12381.q, Bls12381.a, Bls12381.b, Bls12381.gx, Bls12381.gy, Bls12381.g2x, Bls12381.g2y, Bls12381.n,
            Bls12381.h, Bls12381.x, Bls12381.k, Bls12381.sqrtn3, Bls12381.sqrt_n3m1o2
        )

val defaultEcTwist
    get() =
        EC(
            Bls12381.q,
            Bls12381.aTwist,
            Bls12381.bTwist,
            Bls12381.gx,
            Bls12381.gy,
            Bls12381.g2x,
            Bls12381.g2y,
            Bls12381.n,
            Bls12381.h,
            Bls12381.x,
            Bls12381.k,
            Bls12381.sqrtn3,
            Bls12381.sqrt_n3m1o2
        )


@ExperimentalUnsignedTypes
data class AffinePoint(val x: Field, val y: Field, val infinity: Boolean, val ec: EC) {
    val FE = getCompanionFromClass(x::class)

    init {
        if (x::class != y::class) {
            throw IllegalArgumentException()
        }
    }

    operator fun plus(other: AffinePoint): AffinePoint {
        return addPoints(this, other, this.ec, this.FE)
    }

    operator fun times(other: BigInteger): AffinePoint {
        return scalarMultiJacobian(other, this.toJacobian(), this.ec).toAffine()
    }

    operator fun times(other: Int): AffinePoint {
        return times(BigInteger(other))
    }

    operator fun times(fq: Fq): AffinePoint {
        return scalarMultiJacobian(fq.value.residue, toJacobian(), ec).toAffine()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AffinePoint) return false
        return x == other.x && y == other.y && infinity == other.infinity
    }

    val isOnCurve: Boolean
        get() {
            if (infinity) return true
            val left = y * y
            val right = x * x * x + ec.a * x + ec.b
            return left == right
        }

    fun toJacobian(): JacobianPoint {
        return JacobianPoint(x, y, FE.one(ec.q), infinity, ec)
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + infinity.hashCode()
        return result
    }

    operator fun unaryMinus(): AffinePoint {
        return AffinePoint(x, -y, infinity, ec)
    }


}

@ExperimentalUnsignedTypes
class JacobianPoint(
    val x: Field,
    val y: Field,
    val z: Field,
    val infinity: Boolean,
    val ec: EC
) {
    val FE = getCompanionFromClass(x::class)


    fun isOnCurve(): Boolean {
        if (this.infinity) return true
        else {
            return this.toAffine().isOnCurve
        }
    }

    fun toAffine(): AffinePoint {
        if (infinity) {
            return AffinePoint(Fq.zero(ec.q), Fq.zero(ec.q), infinity, ec)
        }
        val newX = x / z.pow(2)
        val newY = y / z.pow(3)
        return AffinePoint(newX, newY, infinity, ec)
    }

    fun checkValid() {
        if (!isOnCurve()) throw Exception()
        if (!(this * this.ec.n == G2Infinity())) throw Exception()
    }

    fun toByteArray(): ByteArray {
        return pointToBytes(this, this.ec, this.FE).toByteArray()
    }

    fun toUByteArray(): UByteArray {
        return pointToBytes(this, this.ec, this.FE)
    }

    fun getFingerprint(): BigInteger {
        val ser = this.toByteArray()
        return BigInteger.fromByteArray(Sha256().digest(ser).take(4).toByteArray(), Sign.POSITIVE)
    }


    override fun equals(other: Any?): Boolean {
        if (other !is JacobianPoint) return false
        return this.toAffine() == other.toAffine()
    }

    operator fun plus(other: JacobianPoint): JacobianPoint {
        return addPointsJacobian(this, other, this.ec, this.FE)
    }

    operator fun times(other: BigInteger): JacobianPoint {
        return scalarMultiJacobian(other, this, this.ec)
    }

    operator fun times(other: Int): JacobianPoint {
        return scalarMultiJacobian(BigInteger(other), this, this.ec)
    }

    operator fun times(other: Fq): JacobianPoint {
        return scalarMultiJacobian(other.value.residue, this, this.ec)
    }

    override fun toString(): String = "JacobianPoint(x=$x, y=$y, z=$z, i=$infinity)"
    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + infinity.hashCode()
        return result
    }

    operator fun unaryMinus(): JacobianPoint {
        return this.toAffine().unaryMinus().toJacobian()
    }
}

private fun signFq(element: Fq, ec: EC = defaultEc): Boolean {
    return element > Fq(ec.q, (ec.q - 1) / 2)
}

private fun signFq2(element: Fq2, ec: EC = defaultEcTwist): Boolean {
    if (element.b == Fq(ec.q, 0)) {
        return signFq(element.a)
    }
    return element.b > Fq(ec.q, ((ec.q - 1) / 2))
}

@ExperimentalUnsignedTypes
private fun pointToBytes(pointJ: JacobianPoint, ec: EC, FE: FieldCompanion): UByteArray {
    val point = pointJ.toAffine()
    val output = point.x.toUByteArray()

    if (point.infinity) {
        return ubyteArrayOf(0x40u) + UByteArray(output.size - 1) { 0u }
    }

    val sign = if (FE == Fq.Companion) {
        signFq(point.y as Fq, ec)
    } else {
        signFq2(point.y as Fq2, ec)
    }

    if (sign) {
        output[0] = output[0].or((0xA0u).toUByte())
    } else {
        output[0] = output[0].or((0x80u).toUByte())
    }
    return output
}

@ExperimentalUnsignedTypes
private fun fromBytesFor(bytes: UByteArray, q: BigInteger, FE: FieldCompanion): Field {
    return if (FE == Fq.Companion) {
        Fq.fromBytes(bytes.toByteArray(), q)
    } else if (FE == Fq2.Companion) {
        Fq2.fromBytes(bytes.toByteArray(), q)
    } else {
        throw Exception("Not Fq or Fq2 (Not a G1 OR G2 Element!)")
    }
}

@ExperimentalUnsignedTypes
private fun bytesTopoint(buffer: UByteArray, ec: EC, FE: FieldCompanion): JacobianPoint {

    // Zcash serialization described in https://datatracker.ietf.org/doc/draft-irtf-cfrg-pairing-friendly-curves/
    if (FE == Fq.Companion) {
        if (buffer.size != 48) throw Exception("G1 Element must be 48 bytes but ${buffer.toHexString()}")
    } else if (FE == Fq2.Companion) {
        if (buffer.size != 96) throw Exception("G2 Element must be 96 bytes")
    } else {
        throw Exception("Not Fq or Fq2 (Not a G1 OR G2 Element!)")
    }


    val mByte = buffer[0].and((0xE0).toUByte())
    if (listOf(0x20, 0x60, 0xE0).contains(mByte.toInt())) {
        throw Exception("Invalid first three bits")
    }
    val cbit = mByte and (0x80).toUByte()
    val iBit = (mByte and (0x40).toUByte())
    val sBit = (mByte and (0x20).toUByte()) > 0u


    if (cbit == (0u).toUByte()) {
        throw Exception("First bit must be 1 (only compressed points")
    }

    val newBuffer = ubyteArrayOf(buffer[0] and (0x1Fu).toUByte()) + buffer.drop(1)

    if (iBit > 0u) {
        val check = buffer.find { it != (0).toUByte() }
        if (check != null) throw IllegalArgumentException("Point at infinity set but data not all zeros")
        else return AffinePoint(FE.zero(ec.q), FE.zero(ec.q), true, ec).toJacobian()
    }

    val x = fromBytesFor(newBuffer, ec.q, FE)
    val yValue = yForX(x, ec, FE)

    val y = if (FE == Fq.Companion) {
        if (signFq(yValue as Fq, ec) == sBit) yValue else -yValue
    } else {
        if (signFq2(yValue as Fq2, ec) == sBit) yValue else -yValue
    }

    return AffinePoint(x, y, false, ec).toJacobian()
}

@ExperimentalUnsignedTypes
internal fun yForX(forX: Field, ec: EC = defaultEc, FE: FieldCompanion): Field {

    val x = forX
    if (!FE.isInstance(x)) {
        TODO("Not implemented yet")
    }
    val u = x * x * x + ec.a * x + ec.b

    val y = u.modSqrt()
    if (y == FE.zero(ec.q) || !AffinePoint(x, y, false, ec).isOnCurve) {
        throw Exception("No y for point x")
    }

    return y
}

@ExperimentalUnsignedTypes
fun G1Generator(ec: EC = defaultEc): JacobianPoint {
    return AffinePoint(ec.gx, ec.gy, false, ec).toJacobian()
}

@ExperimentalUnsignedTypes
fun G2Generator(ec: EC = defaultEcTwist): JacobianPoint {
    return AffinePoint(ec.g2x, ec.g2y, false, ec).toJacobian()
}

@ExperimentalUnsignedTypes
fun G1Infinity(ec: EC = defaultEc, FE: FieldCompanion = Fq.Companion): G1Element {
    return JacobianPoint(FE.one(ec.q), FE.one(ec.q), FE.zero(ec.q), true, ec)
}

@ExperimentalUnsignedTypes
fun G2Infinity(ec: EC = defaultEcTwist, FE: FieldCompanion = Fq2.Companion): G2Element {
    return JacobianPoint(FE.one(ec.q), FE.one(ec.q), FE.zero(ec.q), true, ec)
}

@ExperimentalUnsignedTypes
fun G1FromBytes(bytes: ByteArray, ec: EC = defaultEc, FE: FieldCompanion = Fq.Companion): G1Element {
    return bytesTopoint(bytes.toUByteArray(), ec, FE)
}

@ExperimentalUnsignedTypes
fun G2FromBytes(bytes: ByteArray, ec: EC = defaultEcTwist, FE: FieldCompanion = Fq2.Companion): G2Element {
    return bytesTopoint(bytes.toUByteArray(), ec, FE)
}

/**
 * Given a point on G2 on the twisted curve, this converts it's
 * coordinates back from Fq2 to Fq12. See Craig Costello book, look
 * up twists.
 */
@ExperimentalUnsignedTypes
fun untwist(point: AffinePoint, ec: EC = defaultEc): AffinePoint {
    val f = Fq12.one(ec.q)
    val wsq = Fq12(ec.q, f.root, Fq6.zero(ec.q))
    val wcu = Fq12(ec.q, Fq6.zero(ec.q), f.root)
    return AffinePoint(point.x / wsq, point.y / wcu, false, ec)
}

/**
 * Given an untwisted point, this converts it's
 * coordinates to a point on the twisted curve. See Craig Costello
 * book, look up twists.
 */
@ExperimentalUnsignedTypes
fun twist(point: AffinePoint, ec: EC = defaultEcTwist): AffinePoint {
    val f = Fq12.one(ec.q)
    val wsq = Fq12(ec.q, f.root, Fq6.zero(ec.q))
    val wcu = Fq12(ec.q, Fq6.zero(ec.q), f.root)
    val newX = point.x * wsq
    val newY = point.y * wcu
    return AffinePoint(newX, newY, false, ec)
}

@ExperimentalUnsignedTypes
fun doublePoint(p1: AffinePoint, ec: EC = defaultEc): AffinePoint {
    val x = p1.x
    val y = p1.y
    var left = Fq(ec.q, 3) * x * x
    left *= ec.a
    val s = left / (Fq(ec.q, 2) * y)
    val newX = s * s - x - x
    val newY = s * (x - newX) - y
    return AffinePoint(newX, newY, false, ec)
}

@ExperimentalUnsignedTypes
fun addPoints(
    p1: AffinePoint, p2: AffinePoint, ec: EC = defaultEc,
    FE: FieldCompanion = Fq.Companion
): AffinePoint {
    if (!p1.isOnCurve) throw Exception()
    if (!p2.isOnCurve) throw Exception()

    if (p1.infinity)
        return p2
    if (p2.infinity)
        return p1
    if (p1 == p2)
        return doublePoint(p1, ec)
    if (p1.x == p2.x)
        return AffinePoint(FE.zero(ec.q), FE.zero(ec.q), true, ec)

    val x1 = p1.x
    val y1 = p1.y
    val x2 = p2.x
    val y2 = p2.y
    val s = (y2 - y1) / (x2 - x1)
    val newX = s * s - x1 - x2
    val newY = s * (x1 - newX) - y1
    return AffinePoint(newX, newY, false, ec)
}

@ExperimentalUnsignedTypes
fun doublePointJacobian(
    p1: JacobianPoint, ec: EC = defaultEc,
    FE: FieldCompanion = Fq.Companion
): JacobianPoint {
    val X = p1.x
    val Y = p1.y
    val Z = p1.z
    if (Y == FE.zero(ec.q) || p1.infinity) {
        return JacobianPoint(FE.one(ec.q), FE.one(ec.q), FE.zero(ec.q), true, ec)
    }
    val S = Fq(ec.q, 4) * X * Y * Y

    val zSq = Z * Z
    val z4th = zSq * zSq
    val ySq = Y * Y
    val y4th = ySq * ySq
    var fieldM: Field = Fq(ec.q, 3) * X * X
    fieldM += ec.a * z4th

    val Xp = fieldM * fieldM - Fq(ec.q, 2) * S
    val Yp = fieldM * (S - Xp) - Fq(ec.q, 8) * y4th
    val Zp = Fq(ec.q, 2) * Y * Z

    return JacobianPoint(Xp, Yp, Zp, false, ec)
}

@ExperimentalUnsignedTypes
fun addPointsJacobian(
    p1: JacobianPoint, p2: JacobianPoint, ec: EC = defaultEc,
    FE: FieldCompanion = Fq.Companion
): JacobianPoint {
    if (p1.infinity) return p2
    if (p2.infinity) return p1
    val U1 = p1.x * (p2.z.pow(2))
    val U2 = p2.x * p1.z.pow(2)
    val S1 = p1.y * p2.z.pow(3)
    val S2 = p2.y * p1.z.pow(3)
    if (U1 == U2) {
        if (S1 != S2)
            return JacobianPoint(FE.one(ec.q), FE.one(ec.q), FE.zero(ec.q), true, ec)
        else
            return doublePointJacobian(p1, ec, FE)
    }
    val H = U2 - U1
    val R = S2 - S1
    val Hsq = H * H
    val Hcu = H * Hsq
    val X3 = R * R - Hcu - Fq(ec.q, 2) * U1 * Hsq
    val Y3 = R * (U1 * Hsq - X3) - S1 * Hcu
    val Z3 = H * p1.z * p2.z
    return JacobianPoint(X3, Y3, Z3, false, ec)
}

@ExperimentalUnsignedTypes
private fun scalarMulti(
    c: BigInteger, p1: AffinePoint, ec: EC = defaultEc,
    FE: FieldCompanion = Fq.Companion
): AffinePoint {
    if (p1.infinity || c.mod(ec.q) == BigInteger.ZERO)
        return AffinePoint(FE.zero(ec.q), FE.zero(ec.q), true, ec)
    var result = AffinePoint(FE.zero(ec.q), FE.zero(ec.q), true, ec)
    var addend = p1
    var x = c

    while (x > BigInteger.ZERO) {
        if (x.and(BigInteger.ONE) == BigInteger.ONE) {
            result += addend
        }
        addend += addend
        x = x.shr(1)
    }
    return result
}

@ExperimentalUnsignedTypes
private fun scalarMultiJacobian(
    c: BigInteger, p1: JacobianPoint, ec: EC = defaultEc,
    FE: FieldCompanion = Fq.Companion
): JacobianPoint {
    if (p1.infinity || c.mod(ec.q) == BigInteger.ZERO)
        return JacobianPoint(FE.one(ec.q), FE.one(ec.q), FE.zero(ec.q), true, ec)
    var result = JacobianPoint(FE.one(ec.q), FE.one(ec.q), FE.zero(ec.q), true, ec)
    var addend = p1
    var x = c

    while (x > BigInteger.ZERO) {
        if (x.and(BigInteger.ONE) > BigInteger.ZERO) {
            result += addend
        }
        addend += addend
        x = x.shr(1)
    }

    return result
}


// Isogeny map evaluation specified by map_coeffs
//
// map_coeffs should be specified as (xnum, xden, ynum, yden)
//
// This function evaluates the isogeny over Jacobian projective coordinates.
// For details, see Section 4.3 of
//    Wahby and Boneh, "Fast and simple constant-time hashing to the BLS12-381 elliptic curve."
//    ePrint # 2019/403, https://ia.cr/2019/403.
@ExperimentalUnsignedTypes
fun evalIso(P: JacobianPoint, mapCoeffs: List<List<Fq2>>, ec: EC): JacobianPoint {
    val x = P.x
    val y = P.y
    val z = P.z
    val mapvals = mutableListOf<Fq2?>(null, null, null, null)


    // precompute the required powers of Z^2
    val maxord = mapCoeffs.maxOf { it.size }

    val zpows = MutableList<Fq2?>(maxord) {
        null
    }
    zpows[0] = z.pow(0) as Fq2
    zpows[1] = z.pow(2) as Fq2

    for (idx in 2 until zpows.size) {
        val a = zpows[idx - 1]!!
        val b = zpows[1]!!
        zpows[idx] = (a * b)
    }
    // compute the numerator and denominator of the X and Y maps via Horner's rule
    mapCoeffs.forEachIndexed { idx, coeffs ->
        val coeffsZ = coeffs.reversed().zip(zpows.take(coeffs.size)).map { it.first * it.second!! }

        var tmp: Field = coeffsZ.first()

        for (coeff in coeffsZ.drop(1)) {
            tmp *= x
            tmp += coeff
        }
        mapvals[idx] = tmp as Fq2 // will alwaus be fq2
    }

    // xden is of order 1 less than xnum, so need to multiply it by an extra factor of Z^2
    if (mapCoeffs[1].size + 1 != mapCoeffs[0].size) throw Exception()
    if (zpows[1] == null) throw Exception()
    if (mapvals[1] == null) throw Exception()

    mapvals[1] = (mapvals[1]!! * zpows[1]!!)

    // multiply result of Y map by the y-coordinate y / z^3
    if (mapvals[2] == null) throw Exception()
    if (mapvals[3] == null) throw Exception()
    mapvals[2] = (mapvals[2]!! * y) as Fq2
    mapvals[3] = (mapvals[3]!! * z.pow(3)) as Fq2

    val Z = mapvals[1]!! * mapvals[3]!!
    val X = mapvals[0]!! * mapvals[3]!! * Z
    val Y = mapvals[2]!! * mapvals[1]!! * Z * Z
    return JacobianPoint(X, Y, Z, P.infinity, ec)
}