package util.extensions

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.BigIntegerArithmetic

// equal to X^n mod m. Much faster than doing this linerally.
fun BigInteger.powMod(n: BigInteger, m: BigInteger): BigInteger {
    if (m == BigInteger.ONE) return BigInteger.ZERO

    var r = BigInteger.ONE
    var b = this.mod(m)
    var e = n
    while (e > 0) {
        if (e.mod(BigInteger.TWO) == BigInteger.ONE) {
            r = (r * b).mod(m)
        }
        e = e.shr(1)
        b = (b * b).mod(m)
    }
    return r
}

fun BigInteger.fastPow(n: Int): BigInteger {
    return exponentationBySquaring(BigInteger.ONE, this, BigInteger(n))
}

fun BigInteger.fastPow(n: BigInteger): BigInteger {
    return exponentationBySquaring(BigInteger.ONE, this, n)
}

private tailrec fun exponentationBySquaring(y: BigInteger, x: BigInteger, n: BigInteger): BigInteger {
    return if (n < BigInteger.ZERO) throw UnsupportedOperationException("neg exponent")
    else if (n == BigInteger.ZERO) y
    else if (n == BigInteger.ONE) x * y
    else if (n.mod(BigInteger.TWO) == BigInteger.ZERO) exponentationBySquaring(y, x * x, n / 2)
    else exponentationBySquaring(x * y, x * x, (n - 1) / 2)
}
