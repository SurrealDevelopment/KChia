package bls

import bls.Bls12381.h_eff
import com.ionspin.kotlin.bignum.integer.BigInteger
import util.hexstring.asHexString

object opswug2 {
    private val q = Bls12381.q
    // https://tools.ietf.org/html/draft-irtf-cfrg-hash-to-curve-07#section-4.1
    fun sgn0(x: Fq2): Boolean {
        val sign0 = x.a.value.residue.mod(BigInteger.TWO) > BigInteger.ZERO
        val zero0 = x.a == Fq.zero(x.Q)
        val sign1 = x.b.value.residue.mod(BigInteger.TWO) > BigInteger.ZERO
        return sign0 || (zero0 && sign1)
    }

    val xi2 = Fq2(q, -2, -1)

    val Ell2p_a = Fq2(q, 0, 240)
    val Ell2p_b = Fq2(q, 1012, 1012)

    // eta values, used for computing sqrt(g(X1(t)))
    // For details on how to compute, see ../sage-impl/opt_sswu_g2.sage
    private val ev1 =
        "0x0699BE3B8C6870965E5BF892AD5D2CC7B0E85A117402DFD83B7F4A947E02D978498255A2AAEC0AC627B5AFBDF1BF1C90".asHexString()
            .toBigInteger()
    private val ev2 =
        "0x08157CD83046453F5DD0972B6E3949E4288020B5B8A9CC99CA07E27089A2CE2436D965026ADAD3EF7BABA37F2183E9B5".asHexString()
            .toBigInteger()
    private val ev3 =
        "0x0AB1C2FFDD6C253CA155231EB3E71BA044FD562F6F72BC5BAD5EC46A0B7A3B0247CF08CE6C6317F40EDBC653A72DEE17".asHexString()
            .toBigInteger()
    private val ev4 =
        "0x0AA404866706722864480885D68AD0CCAC1967C7544B447873CC37E0181271E006DF72162A3D3E0287BF597FBF7F8FC1".asHexString()
            .toBigInteger()
    val etas = listOf(Fq2(q, ev1, ev2), Fq2(q, q - ev2, ev1), Fq2(q, ev3, ev4), Fq2(q, q - ev4, ev3))

    //
    // Simplified SWU map, optimized and adapted to Ell2'
    //
    // This function maps an element of Fp^2 to the curve Ell2', 3-isogenous to Ell2.
    fun osswu2_help(t: Fq2): JacobianPoint {
        // first, compute X0(t), detecting and handling exceptional case
        val numDenCommon = xi2.pow(2) * t.pow(4) + xi2 * t.pow(2)
        val x0Num = Ell2p_b * (numDenCommon + Fq(q, 1))
        var x0Den = -Ell2p_a * numDenCommon

        x0Den = if (x0Den == Fq2.zero(q)) Ell2p_a * xi2 else x0Den

        // compute num and den of g(X0(t))
        val gx0Den = x0Den.pow(3)
        var gx0Num: Field = Ell2p_b * gx0Den
        gx0Num += Ell2p_a * x0Num * x0Den.pow(2)
        gx0Num += x0Num.pow(3)

        // try taking sqrt of g(X0(t))
        // this uses the trick for combining division and sqrt from Section 5 of
        // Bernstein, Duif, Lange, Schwabe, and Yang, "High-speed high-security signatures."
        // J Crypt Eng 2(2):77--89, Sept. 2012. http://ed25519.cr.yp.to/ed25519-20110926.pdf
        var tmp1: Field = gx0Den.pow(7)  // v^7
        val tmp2 = gx0Num * tmp1  // u v^7
        tmp1 = tmp1 * tmp2 * gx0Den  // u v^15
        var sqrtCandidate = tmp2 * tmp1.pow((q.pow(2) - 9) / 16)


        // check if g(X0(t)) is square and return the sqrt if so
        for (root in FieldConsts.rootsOfUnity) {
            var y0 = sqrtCandidate * root
            if (y0.pow(2) * gx0Den == gx0Num) {
                // found sqrt(g(X0(t))). force sign of y to equal sign of t
                if (sgn0(y0 as Fq2) != sgn0(t)) {
                    y0 = -y0
                }
                if (sgn0(y0) != sgn0(t)) {
                    throw IllegalStateException()
                }
                return JacobianPoint(x0Num * x0Den, y0 * x0Den.pow(3), x0Den, false, defaultEcTwist)
            }
        }
        // if we've gotten here, then g(X0(t)) is not square. convert srqt_candidate to sqrt(g(X1(t)))
        val x1Num = xi2 * t.pow(2) * x0Num
        val x1Den = x0Den
        val gx1Num = xi2.pow(3) * t.pow(6) * gx0Num
        val gx1_den = gx0Den

        sqrtCandidate *= t.pow(3)

        for (eta in etas) {
            var y1 = eta * sqrtCandidate
            if (y1.pow(2) * gx1_den == gx1Num) {
                // found sqrt(g(X1(t))). force sign of y to equal sign of t
                if (sgn0(y1 as Fq2) != sgn0(t)) {
                    y1 = -y1
                }

                if (sgn0(y1) != sgn0(t)) {
                    throw IllegalStateException()
                }
                return JacobianPoint(x1Num * x1Den, y1 * x1Den.pow(3), x1Den, false, defaultEcTwist)
            }
        }
        throw IllegalArgumentException("osswu2_help failed")

    }

    //
    // 3-Isogeny from Ell2' to Ell2
    //
    // coefficients for the 3-isogeny map from Ell2' to Ell2
    val xnum = listOf(
        Fq2(
            q,
            "0x05C759507E8E333EBB5B7A9A47D7ED8532C52D39FD3A042A88B58423C50AE15D5C2638E343D9C71C6238AAAAAAAA97D6".asHexString()
                .toBigInteger(),
            "0x05C759507E8E333EBB5B7A9A47D7ED8532C52D39FD3A042A88B58423C50AE15D5C2638E343D9C71C6238AAAAAAAA97D6".asHexString()
                .toBigInteger(),
        ),
        Fq2(
            q,
            BigInteger.ZERO,
            "0x11560BF17BAA99BC32126FCED787C88F984F87ADF7AE0C7F9A208C6B4F20A4181472AAA9CB8D555526A9FFFFFFFFC71A".asHexString()
                .toBigInteger(),
        ),
        Fq2(
            q,
            "0x11560BF17BAA99BC32126FCED787C88F984F87ADF7AE0C7F9A208C6B4F20A4181472AAA9CB8D555526A9FFFFFFFFC71E".asHexString()
                .toBigInteger(),
            "0x08AB05F8BDD54CDE190937E76BC3E447CC27C3D6FBD7063FCD104635A790520C0A395554E5C6AAAA9354FFFFFFFFE38D".asHexString()
                .toBigInteger(),
        ),
        Fq2(
            q,
            "0x171D6541FA38CCFAED6DEA691F5FB614CB14B4E7F4E810AA22D6108F142B85757098E38D0F671C7188E2AAAAAAAA5ED1".asHexString()
                .toBigInteger(),
            "0x00".asHexString().toBigInteger(),
        ),
    )
    val xden = listOf(
        Fq2(
            q,
            "0x00".asHexString().toBigInteger(),
            "0x1A0111EA397FE69A4B1BA7B6434BACD764774B84F38512BF6730D2A0F6B0F6241EABFFFEB153FFFFB9FEFFFFFFFFAA63".asHexString()
                .toBigInteger(),
        ),
        Fq2(
            q,
            "0x0C".asHexString().toBigInteger(),
            "0x1A0111EA397FE69A4B1BA7B6434BACD764774B84F38512BF6730D2A0F6B0F6241EABFFFEB153FFFFB9FEFFFFFFFFAA9F".asHexString()
                .toBigInteger(),
        ),
        Fq2(q, 0x1, 0x0),
    )
    val ynum = listOf(
        Fq2(
            q,
            "0x1530477C7AB4113B59A4C18B076D11930F7DA5D4A07F649BF54439D87D27E500FC8C25EBF8C92F6812CFC71C71C6D706".asHexString()
                .toBigInteger(),
            "0x1530477C7AB4113B59A4C18B076D11930F7DA5D4A07F649BF54439D87D27E500FC8C25EBF8C92F6812CFC71C71C6D706".asHexString()
                .toBigInteger(),
        ),
        Fq2(
            q,
            "0x00".asHexString().toBigInteger(),
            "0x05C759507E8E333EBB5B7A9A47D7ED8532C52D39FD3A042A88B58423C50AE15D5C2638E343D9C71C6238AAAAAAAA97BE".asHexString()
                .toBigInteger(),
        ),
        Fq2(
            q,
            "0x11560BF17BAA99BC32126FCED787C88F984F87ADF7AE0C7F9A208C6B4F20A4181472AAA9CB8D555526A9FFFFFFFFC71C".asHexString()
                .toBigInteger(),
            "0x08AB05F8BDD54CDE190937E76BC3E447CC27C3D6FBD7063FCD104635A790520C0A395554E5C6AAAA9354FFFFFFFFE38F".asHexString()
                .toBigInteger(),
        ),
        Fq2(
            q,
            "0x124C9AD43B6CF79BFBF7043DE3811AD0761B0F37A1E26286B0E977C69AA274524E79097A56DC4BD9E1B371C71C718B10".asHexString()
                .toBigInteger(),
            "0x00".asHexString().toBigInteger(),
        ),
    )
    val yden = listOf(
        Fq2(
            q,
            "0x1A0111EA397FE69A4B1BA7B6434BACD764774B84F38512BF6730D2A0F6B0F6241EABFFFEB153FFFFB9FEFFFFFFFFA8FB".asHexString()
                .toBigInteger(),
            "0x1A0111EA397FE69A4B1BA7B6434BACD764774B84F38512BF6730D2A0F6B0F6241EABFFFEB153FFFFB9FEFFFFFFFFA8FB".asHexString()
                .toBigInteger(),
        ),
        Fq2(
            q,
            "0x00".asHexString().toBigInteger(),
            "0x1A0111EA397FE69A4B1BA7B6434BACD764774B84F38512BF6730D2A0F6B0F6241EABFFFEB153FFFFB9FEFFFFFFFFA9D3".asHexString()
                .toBigInteger(),
        ),
        Fq2(
            q,
            "0x12".asHexString().toBigInteger(),
            "0x1A0111EA397FE69A4B1BA7B6434BACD764774B84F38512BF6730D2A0F6B0F6241EABFFFEB153FFFFB9FEFFFFFFFFAA99".asHexString()
                .toBigInteger(),
        ),
        Fq2(q, 0x1, 0x0),
    )


    // compute 3-isogeny map from Ell2' to Ell2
    fun iso3(P: JacobianPoint): JacobianPoint = evalIso(P, listOf(xnum, xden, ynum, yden), defaultEcTwist)

    // map from Fq2 element(s) to point in G2 subgroup of Ell2
    fun optSwu2Map(t: Fq2, t2: Fq2? = null): JacobianPoint {
        var Pp = iso3(osswu2_help(t))
        if (t2 != null) {
            val Pp2 = iso3(osswu2_help(t2))
            Pp += Pp2
        }
        return Pp * h_eff
    }

    fun g2Map(alpha: ByteArray, dst: ByteArray): JacobianPoint {
        val hp2s = hashToFields.Hp2(alpha, 2, dst)

        val fqs = hp2s.map { list -> list.map { Fq(q, it) } }
        val fq2s = fqs.map { Fq2.one(q).fromArgs(q, it) }
        return optSwu2Map(fq2s[0], fq2s[1])
    }


}