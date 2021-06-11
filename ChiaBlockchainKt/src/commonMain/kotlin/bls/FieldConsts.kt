package bls

import com.ionspin.kotlin.bignum.integer.BigInteger

object FieldConsts {

    val bls12381Q = BigInteger.parseString(
        "1A0111EA397FE69A4B1BA7B6434BACD764774B84F38512BF6730D2A0F6B0F6241EABFF" +
                "FEB153FFFFB9FEFFFFFFFFAAAB", 16
    )
    val q = bls12381Q
    val rv1 = BigInteger.parseString(
        "6AF0E0437FF400B6831E36D6BD17FFE48395DABC2D3435E77F76E17009241C5EE67992F72EC0" +
                "5F4C81084FBEDE3CC09", 16
    )

    val rootsOfUnity = listOf(
        Fq2(q, 1, 0), Fq2(q, 0, 1), Fq2(q, rv1, rv1), Fq2(q, rv1, q - rv1)
    )

    val frob_coeffs = mapOf(
        Triple(2, 1, 1) to Fq(q, -1),
        Triple(6, 1, 1) to Fq2(
            q,
            Fq(q, 0x0),
            Fq(
                q,
                BigInteger.parseString(
                    "1A0111EA397FE699EC02408663D4DE85AA0D857D89759AD4897D29650FB85F9B409427EB4" +
                            "F49FFFD8BFD00000000AAAC", 16
                ),
            ),
        ),  // noga: E501
        Triple(6, 1, 2) to Fq2(
            q,
            Fq(
                q,
                BigInteger.parseString(
                    "1A0111EA397FE699EC02408663D4DE85AA0D857D89759AD4897D29650FB85F9B409427EB4F49FFFD8" +
                            "BFD00000000AAAD", 16
                )
            ),
            Fq(q, 0x0),
        ),  // noga: E501
        Triple(6, 2, 1) to Fq2(
            q,
            Fq(
                q,
                BigInteger.parseString(
                    "5F19672FDF76CE51BA69C6076A0F77EADDB3A93BE6F89688DE17D813620A00022E" +
                            "01FFFFFFFEFFFE", 16
                )
            ),
            Fq(q, 0x0),
        ),  // noga: E501
        Triple(6, 2, 2) to Fq2(
            q,
            Fq(
                q,
                BigInteger.parseString(
                    "1A0111EA397FE699EC02408663D4DE85AA0D857D89759AD4897D29650FB85F9B409" +
                            "427EB4F49FFFD8BFD00000000AAAC", 16
                )
            ),
            Fq(q, 0x0),
        ),
        Triple(6, 3, 1) to Fq2(q, Fq(q, 0x0), Fq(q, 0x1)),
        Triple(6, 3, 2) to Fq2(
            q,
            Fq(
                q,
                BigInteger.parseString(
                    "1A0111EA397FE69A4B1BA7B6434BACD764774B84F38512BF6730D2A0F6B0F6241EA" +
                            "BFFFEB153FFFFB9FEFFFFFFFFAAAA", 16
                )
            ),
            Fq(q, 0x0),
        ),
        Triple(6, 4, 1) to Fq2(
            q,
            Fq(
                q,
                BigInteger.parseString(
                    "1A0111EA397FE699EC02408663D4DE85AA0D857D89759AD4897D29650FB85F9B409" +
                            "427EB4F49FFFD8BFD00000000AAAC", 16
                )
            ),
            Fq(q, 0x0),
        ),
        Triple(6, 4, 2) to Fq2(
            q,
            Fq(
                q,
                BigInteger.parseString(
                    "5F19672FDF76CE51BA69C6076A0F77EADDB3A93BE6F89688DE17D813620A00022E0" +
                            "1FFFFFFFEFFFE", 16
                )
            ),
            Fq(q, 0x0),
        ),
        Triple(6, 5, 1) to Fq2(
            q,
            Fq(q, 0x0),
            Fq(
                q,
                BigInteger.parseString(
                    "5F19672FDF76CE51BA69C6076A0F77EADDB3A93BE6F89688DE17D813620A00022E01" +
                            "FFFFFFFEFFFE", 16
                )
            ),
        ),
        Triple(6, 5, 2) to Fq2(
            q,
            Fq(
                q,
                BigInteger.parseString(
                    "5F19672FDF76CE51BA69C6076A0F77EADDB3A93BE6F89688DE17D813620A00022E01F" +
                            "FFFFFFEFFFF", 16
                )
            ),
            Fq(q, 0x0),
        ),
        Triple(12, 1, 1) to Fq6(
            q,
            Fq2(
                q,
                Fq(
                    q,
                    BigInteger.parseString(
                        "1904D3BF02BB0667C231BEB4202C0D1F0FD603FD3CBD5F4F7B2443D784BAB9C4F6" +
                                "7EA53D63E7813D8D0775ED92235FB8", 16
                    )
                ),
                Fq(
                    q,
                    BigInteger.parseString(
                        "FC3E2B36C4E03288E9E902231F9FB854A14787B6C7B36FEC0C8EC971F63C5F282D5" +
                                "AC14D6C7EC22CF78A126DDC4AF3", 16
                    )
                ),
            ),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
        ),
        Triple(12, 2, 1) to Fq6(
            q,
            Fq2(
                q,
                Fq(
                    q,
                    BigInteger.parseString(
                        "5F19672FDF76CE51BA69C6076A0F77EADDB3A93BE6F89688DE17D813620A000" +
                                "22E01FFFFFFFEFFFF", 16
                    )
                ),
                Fq(q, 0x0),
            ),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
        ),
        Triple(12, 3, 1) to Fq6(
            q,
            Fq2(
                q,
                Fq(
                    q,
                    BigInteger.parseString(
                        "135203E60180A68EE2E9C448D77A2CD91C3DEDD930B1CF60EF396489F61EB45E3" +
                                "04466CF3E67FA0AF1EE7B04121BDEA2", 16
                    )
                ),
                Fq(
                    q,
                    BigInteger.parseString(
                        "6AF0E0437FF400B6831E36D6BD17FFE48395DABC2D3435E77F76E17009241C5EE" +
                                "67992F72EC05F4C81084FBEDE3CC09", 16
                    )
                ),
            ),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
        ),
        Triple(12, 4, 1) to Fq6(
            q,
            Fq2(
                q,
                Fq(
                    q,
                    BigInteger.parseString(
                        "5F19672FDF76CE51BA69C6076A0F77EADDB3A93BE6F89688DE17D813620A00" +
                                "022E01FFFFFFFEFFFE", 16
                    )
                ),
                Fq(q, 0x0),
            ),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
        ),
        Triple(12, 5, 1) to Fq6(
            q,
            Fq2(
                q,
                Fq(
                    q,
                    BigInteger.parseString(
                        "144E4211384586C16BD3AD4AFA99CC9170DF3560E77982D0DB45F3536814F0BD" +
                                "5871C1908BD478CD1EE605167FF82995", 16
                    )
                ),
                Fq(
                    q,
                    BigInteger.parseString(
                        "5B2CFD9013A5FD8DF47FA6B48B1E045F39816240C0B8FEE8BEADF4D8E9C0566C" +
                                "63A3E6E257F87329B18FAE980078116", 16
                    )
                ),
            ),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
        ),
        Triple(12, 6, 1) to Fq6(
            q,
            Fq2(
                q,
                Fq(
                    q,
                    BigInteger.parseString(
                        "1A0111EA397FE69A4B1BA7B6434BACD764774B84F38512BF6730D2A0F6B0F624" +
                                "1EABFFFEB153FFFFB9FEFFFFFFFFAAAA", 16
                    )
                ),
                Fq(q, 0x0),
            ),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
        ),
        Triple(12, 7, 1) to Fq6(
            q,
            Fq2(
                q,
                Fq(
                    q,
                    BigInteger.parseString(
                        "FC3E2B36C4E03288E9E902231F9FB854A14787B6C7B36FEC0C8EC971F63C5F28" +
                                "2D5AC14D6C7EC22CF78A126DDC4AF3", 16
                    )
                ),
                Fq(
                    q,
                    BigInteger.parseString(
                        "1904D3BF02BB0667C231BEB4202C0D1F0FD603FD3CBD5F4F7B2443D784BAB9C4" +
                                "F67EA53D63E7813D8D0775ED92235FB8", 16
                    )
                ),
            ),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
        ),
        Triple(12, 8, 1) to Fq6(
            q,
            Fq2(
                q,
                Fq(
                    q,
                    BigInteger.parseString(
                        "1A0111EA397FE699EC02408663D4DE85AA0D857D89759AD4897D29650FB85F9B" +
                                "409427EB4F49FFFD8BFD00000000AAAC", 16
                    )
                ),
                Fq(q, 0x0),
            ),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
        ),
        Triple(12, 9, 1) to Fq6(
            q,
            Fq2(
                q,
                Fq(
                    q,
                    BigInteger.parseString(
                        "6AF0E0437FF400B6831E36D6BD17FFE48395DABC2D3435E77F76E17009241C5E" +
                                "E67992F72EC05F4C81084FBEDE3CC09", 16
                    )
                ),
                Fq(
                    q,
                    BigInteger.parseString(
                        "135203E60180A68EE2E9C448D77A2CD91C3DEDD930B1CF60EF396489F61EB45E" +
                                "304466CF3E67FA0AF1EE7B04121BDEA2", 16
                    )
                ),
            ),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
        ),
        Triple(12, 10, 1) to Fq6(
            q,
            Fq2(
                q,
                Fq(
                    q,
                    BigInteger.parseString(
                        "1A0111EA397FE699EC02408663D4DE85AA0D857D89759AD4897D29650FB85F9B" +
                                "409427EB4F49FFFD8BFD00000000AAAD", 16
                    )
                ),
                Fq(q, 0x0),
            ),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
        ),
        Triple(12, 11, 1) to Fq6(
            q,
            Fq2(
                q,
                Fq(
                    q,
                    BigInteger.parseString(
                        "5B2CFD9013A5FD8DF47FA6B48B1E045F39816240C0B8FEE8BEADF4D8E9C0566C" +
                                "63A3E6E257F87329B18FAE980078116", 16
                    )
                ),
                Fq(
                    q,
                    BigInteger.parseString(
                        "144E4211384586C16BD3AD4AFA99CC9170DF3560E77982D0DB45F3536814F0BD" +
                                "5871C1908BD478CD1EE605167FF82995", 16
                    )
                ),
            ),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
            Fq2(q, Fq(q, 0x0), Fq(q, 0x0)),
        )
    )

}