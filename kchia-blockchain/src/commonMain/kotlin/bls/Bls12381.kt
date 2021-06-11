package bls

import com.ionspin.kotlin.bignum.integer.BigInteger

object Bls12381 {
    val x = BigInteger.parseString("D201000000010000", 16) * -1

    val q = BigInteger.parseString(
        "1A0111EA397FE69A4B1BA7B6434BACD764774B84F38512BF6730D2A0F6B0F6241EABFFFEB153FFFFB9FEFFFFFFFFAAAB",
        16
    )

    val a = Fq(q, 0)
    val b = Fq(q, 4)
    val aTwist = Fq2(q, 0, 0)
    val bTwist = Fq2(q, 4, 4)

    // generators for g1, g2
    val gx = Fq(
        q,
        BigInteger.parseString(
            "17F1D3A73197D7942695638C4FA9AC0FC3688C4F9774B905A14E3A3F171BAC586C55E83FF97A1AEFFB3AF00ADB22C6BB",
            16
        )
    )
    val gy = Fq(
        q,
        BigInteger.parseString(
            "08B3F481E3AAA0F1A09E30ED741D8AE4FCF5E095D5D00AF600DB18CB2C04B3EDD03CC744A2888AE40CAA232946C5E7E1",
            16
        )
    )

    val g2x = Fq2(
        q,
        BigInteger.parseString(
            "352701069587466618187139116011060144890029952792775240219908644239793785735715026873347600343865175952761926303160",
            10
        ),
        BigInteger.parseString(
            "3059144344244213709971259814753781636986470325476647558659373206291635324768958432433509563104347017837885763365758",
            10
        )
    )
    val g2y = Fq2(
        q,
        BigInteger.parseString(
            "1985150602287291935568054521177171638300868978215655730859378665066344726373823718423869104263333984641494340347905",
            10
        ),
        BigInteger.parseString(
            "927553665492332455747201965776037880757740193453592970025027978793976877002675564980949289727957565575433344219582",
            10
        )
    )

    val n = BigInteger.parseString("73EDA753299D7D483339D80809A1D80553BDA402FFFE5BFEFFFFFFFF00000001", 16)
    val h = BigInteger.parseString("396C8C005555E1568C00AAAB0000AAAB", 16)
    val h_eff = BigInteger.parseString(
        "BC69F08F2EE75B3584C6A0EA91B352888E2A8E9145AD7689986FF031508FFE1329C2F178731DB956D82BF015D1212B02EC0EC69D7477C1AE954CBC06689F6A359894C0ADEBBF6B4E8020005AAA95551",
        16
    )
    val k = BigInteger(12)

    val sqrtn3 = BigInteger.parseString(
        "1586958781458431025242759403266842894121773480562120986020912974854563298150952611241517463240701",
        10
    )
    val sqrt_n3m1o2 = BigInteger.parseString(
        "793479390729215512621379701633421447060886740281060493010456487427281649075476305620758731620350",
        10
    )

}