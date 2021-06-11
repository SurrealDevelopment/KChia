package bls

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.modular.ModularBigInteger
import util.extensions.fixSize
import util.extensions.powMod
import util.hexstring.toHexString
import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

@JvmInline
value class Fq(val value: ModularBigInteger) : Field {

    override val Q: BigInteger
        get() = value.modulus

    constructor(Q: Int, value: Int) : this(BigInteger(value).toModularBigInteger(BigInteger(Q)))
    constructor(Q: BigInteger, value: Int) : this(BigInteger(value).toModularBigInteger(Q))
    constructor(Q: BigInteger, value: BigInteger) : this(value.toModularBigInteger(Q))

    override val extension: Int get() = 1
    override fun plusImp(other: BigInteger): Fq = Fq(value + other.toModularBigInteger(Q))
    override fun plusImp(other: Int): Fq = Fq(value + BigInteger(other).toModularBigInteger(Q))
    override fun plusImp(other: Field): Field {
        return if (other is Fq)
            Fq(value + other.value)
        else if (other is FieldExt) {
            // rearrange
            other.plusImp(this)
        } else {
            throw IllegalStateException("UNSUPPORTED FBASE")
        }
    }

    override fun compareTo(other: Field): Int {
        if (other !is Fq) throw IllegalArgumentException()
        return value.compareTo(other.value)
    }


    override fun unaryMinusImp(): Fq {
        return  Fq(Q, value.residue.unaryMinus())
    }
    override fun minusImp(other: BigInteger): Fq = Fq(value - other.toModularBigInteger(Q))
    override fun minusImp(other: Int): Fq = Fq(value - BigInteger(other).toModularBigInteger(Q))
    override fun minusImp(other: Field): Field {
        return if (other is Fq)
            Fq(value - other.value)
        else if (other is FieldExt) {
            // rearrange and flip
            this.plusImp(other.unaryMinusImp())
        } else {
            throw NotImplementedError("Not valid arg $other")
        }
    }

    override fun timesImp(other: BigInteger): Fq = Fq(value * other.toModularBigInteger(Q))
    override fun timesImp(other: Int): Fq = Fq(value * BigInteger(other).toModularBigInteger(Q))
    override fun timesImp(other: Field): Field {
        if (other is Fq) {
            return Fq(value * other.value)
        } else if (other is FieldExt) {
            // let field ext handle
            return other.timesImp(this)
        }
        throw NotImplementedError("Not supported for ${other::class}")
    }

    override fun toString(): String {
        val s = this.value.toByteArray().toHexString()
        return if (s.length > 10) {
            "Fq(${s.take(5)}...${s.takeLast(5)})"
        } else {
            "Fq($s)"
        }
    }

    override fun powImp(n: Int): Fq = pow(BigInteger(n))
    override fun powImp(n: BigInteger): Fq {
        if (n == BigInteger.ZERO) {
            return Fq(Q, 1)
        } else if (n == BigInteger.ONE) {
            return this
        } else if (n.mod(BigInteger.TWO) == BigInteger.ZERO) {
            return Fq(value * value).pow(n / 2)
        } else {
            return Fq(value * value).pow(n / 2).timesImp(this) as Fq
        }
    }

    override fun qiPowerImp(n: Int): Fq {
        return this
    }

    override fun invertImp(): Fq {
        return Fq(this.value.inverse())
    }

    override fun divImp(other: Field): Field {
        if (other is Fq) {
            return this.timesImp(!other)
        } else if (other is FieldExt) {
            // let field ext handle
            return other.divImp(this)
        }
        throw NotImplementedError("Not supported for ${other::class}")
    }

    override fun divImp(other: BigInteger): Fq = this.timesImp(!Fq(Q, other)) as Fq
    override fun divImp(other: Int): Fq = this.timesImp(!Fq(Q, other)) as Fq

    override fun modSqrtImp(): Fq {

        if (value.residue == BigInteger.ZERO)
            return Fq(Q, BigInteger.ZERO)
        if (value.pow((Q - BigInteger.ONE) / BigInteger.TWO).residue != BigInteger.ONE)
            throw Exception("No sqrt")
        if (Q.mod(BigInteger(4)) == BigInteger(3))
            return Fq(value.pow((Q + BigInteger.ONE).div(4)))
        if (Q.mod(BigInteger(8)) == BigInteger(5))
            return Fq(value.pow((Q + BigInteger(3)).div(8)))

        // p % 8 == 1. Tonelli Shanks algorithm for finding square root
        var S = BigInteger.ZERO
        var q = this.Q.minus(1)

        while (q.mod(BigInteger(2)) == BigInteger(0)) {
            q = q.div(2)
            S += BigInteger.ONE
        }
        var z = BigInteger.ZERO
        for (i in BigInteger.ZERO..Q - 1) {
            val euler = i.powMod(Q.minus(1).div(2), Q)
            if (euler == BigInteger.ONE.negate().mod(Q)) {
                z = i
                break
            }
        }

        var M = S
        var c = z.powMod(q, Q)
        var t = value.pow(q)
        var R = value.pow((q + 1) / 2)

        while (true) {
            if (t.residue == BigInteger.ZERO)
                return Fq(Q, 0)
            if (t.residue == BigInteger.ONE)
                return Fq(R)
            var i = BigInteger.ZERO
            var f = t
            while (f.residue != BigInteger.ONE) {
                f = f.pow(BigInteger.TWO)
                i += 1
            }
            val b = c.powMod(BigInteger(2).powMod(M - i - 1, Q), Q)
            M = i
            c = b.powMod(BigInteger.TWO, Q)
            t = (t.residue * c).toModularBigInteger(Q)
            R = (R.residue * b).toModularBigInteger(Q)
        }

    }

    override fun companion(): FieldCompanion {
        return Companion
    }

    override val flatSequence: Sequence<Fq>
        get() = sequence

    override val sequence: Sequence<Fq>
        get() = sequenceOf(this) // pretty simple


    override fun toByteArray(): ByteArray {
        return value.toByteArray().fixSize(48)
    }

    override fun toUByteArray(): UByteArray {
        return value.toUByteArray().fixSize(48)
    }

    companion object : FieldCompanion {
        override fun zero(Q: BigInteger): Fq = Fq(Q, 0)
        override fun one(Q: BigInteger): Fq = Fq(Q, 1)

        override val base: FieldCompanion?
            get() = null

        override fun fromFq(Q: BigInteger, fq: Fq): Fq {
            return fq
        }

        fun fromBytes(buffer: ByteArray, q: BigInteger): Fq {
            return Fq(q, BigInteger.fromByteArray(buffer, sign = Sign.POSITIVE))
        }

        override fun isInstance(any: Any): Boolean {
            return Fq::class.isInstance(any)
        }

    }

}

abstract class FieldExt(override val Q: BigInteger) : Field {
    abstract val root: Field
    abstract val embedding: Int
    abstract fun fromArgs(Q: BigInteger, args: List<Field>): Field
    abstract override val sequence: Sequence<Field>

    override fun toString(): String {
        return "Fq(${this.extension}(${this.sequence.joinToString(",")})"
    }

    override fun plusImp(other: BigInteger): Field {
        val otherNew = Array(sequence.count()) {
            this.companion().base!!.zero(Q)
        }
        otherNew[0] = (otherNew[0] plusImp other)
        return plusImp(fromArgs(Q, otherNew.toList()))
    }

    override fun plusImp(other: Int): Field {
        return this plusImp (BigInteger(other))
    }

    override fun plusImp(other: Field): Field {

        if (other.extension > this.extension) {
            return other.plusImp(this) // flippidy flop
        }
        val zip = if (this.companion().isInstance(other)) {
            sequence.zip((other).sequence)
        } else {
            // must convert
            val new = this.companion().zero(Q).sequence.toMutableList()
            new[0] = (new[0].plusImp(other))

            sequence.zip(new.toList().asSequence())
        }

        return this.fromArgs(Q, zip.map { (it.first.plusImp(it.second)) }.toList())

    }

    override fun unaryMinusImp(): Field {
        return fromArgs(Q, sequence.map { it.unaryMinusImp() }.toList())
    }

    override fun minusImp(other: BigInteger): Field = this + (-other)
    override fun minusImp(other: Int): Field = this + (-other)
    override fun minusImp(other: Field): Field = this.plusImp(-other)

    override fun timesImp(other: BigInteger): Field {
        return fromArgs(Q, sequence.map { it * other }.toList())
    }

    override fun timesImp(other: Int): Field {
        return fromArgs(Q, sequence.map { it * other }.toList())
    }

    override fun timesImp(other: Field): Field {
        if (this.extension < other.extension) {
            // go other way
            return other.timesImp(this)
        }
        val buffer = MutableList(this.sequence.count()) {
            this.companion().base!!.zero(Q)
        }

        this.sequence.forEachIndexed { i, x ->
            if (this.extension == other.extension) {
                other.sequence.forEachIndexed { j, y ->
                    if (i + j >= this.embedding) {
                        buffer[(i + j).rem(this.embedding)] =
                            buffer[(i + j).rem(this.embedding)]
                                .plusImp(x.timesImp(y).timesImp(root))
                    } else {
                        buffer[(i + j).rem(this.embedding)] =
                            buffer[(i + j).rem(this.embedding)].plusImp(x.timesImp(y))
                    }
                }
            } else {

                buffer[i] = (x.timesImp(other))
            }
        }

        return this.fromArgs(Q, buffer)
    }


    override fun compareTo(other: Field): Int {
        val a = toByteArray()
        val b = other.toByteArray()
        return BigInteger.fromByteArray(a, Sign.POSITIVE).compareTo(
            BigInteger.fromByteArray(b, Sign.POSITIVE)
        )
    }

    override fun equals(other: Any?): Boolean {

        if (other == null) {
            return false
        } else if (companion().isInstance(other)) {
            // just compare our flattened tree to theirs and q
            val bf = (other as FieldExt).flatten()
            val zip = this.flatten().zip(bf)
            val equiv = zip.firstOrNull {
                it.first.value != it.second.value
            }
            return (equiv == null && other.Q == this.Q)
        } else if (other is FieldExt && extension <= other.extension) {
            // flip sides
            return other == this
        } else if (other is FieldExt || other is BigInteger || other is Int) {
            // check fist and only first. Rest of this should be zero
            val check = sequence.drop(1).find { it != companion().base!!.zero(Q) } == null
            if (!check) return false
            return sequence.first() == other
        } else {
            throw NotImplementedError()
        }
    }

    override val flatSequence: Sequence<Fq>
        get() {
            val seq = this.sequence
            val newBases = seq
            return if (newBases.first() is Fq) {
                newBases.map { it as Fq } // this is our sequence.Already flat
            } else {
                // recursively call our members then flatten
                newBases.map { it.flatSequence }.flatten()
            }
        }

    fun flatten(): List<Fq> {
        return flatSequence.toList()
    }

    override fun powImp(n: Int): Field {
        return this.pow(BigInteger(n))
    }

    override fun qiPowerImp(n: Int): Field {
        if (this.Q != FieldConsts.bls12381Q) {
            throw NotImplementedError()
        }
        val i = n.rem(this.extension)
        if (i == 0) return this
        return this.fromArgs(
            this.Q,
            this.sequence.mapIndexed { index, element ->
                if (index > 0) {
                    val frob = FieldConsts.frob_coeffs[Triple(extension, i, index)]!!
                    element.qiPower(i).timesImp(frob)
                } else element.qiPower(i)
            }.toList()
        )
    }

    override fun divImp(other: BigInteger): Field {
        throw NotImplementedError()
    }

    override fun divImp(other: Int): Field {
        throw NotImplementedError()
    }

    override fun divImp(other: Field): Field {
        return this.timesImp(other.invertImp())
    }

    override fun powImp(n: BigInteger): Field {
        var ans = this.companion().one(Q)
        var base: Field = this

        var e = n
        while (e > 0) {
            if (e.and(BigInteger.ONE) == BigInteger.ONE)
                ans = ans.timesImp(base)
            base = base.timesImp(base)
            e = e.shr(1)
        }
        return ans
    }

    override fun toByteArray(): ByteArray {
        val bytes = ArrayList<Byte>()
        this.sequence.toList().reversed().forEach {
            bytes.addAll(it.toByteArray().toList())
        }
        return bytes.toByteArray()
    }

    override fun toUByteArray(): UByteArray {
        val bytes = ArrayList<UByte>()
        this.sequence.toList().reversed().forEach {
            bytes.addAll(it.toUByteArray().toList())
        }
        return bytes.toUByteArray()
    }

    override fun hashCode(): Int {
        var result = Q.hashCode()
        this.flatten().forEach {
            result = result * 31 + it.value.hashCode()
        }
        return result
    }
}


class Fq2(Q: BigInteger, val pair: Pair<Fq, Fq>) : FieldExt(Q) {
    constructor(Q: BigInteger, a: Fq, b: Fq) : this(Q, Pair(a, b))
    constructor(Q: BigInteger, a: Int, b: Int) : this(Q, Pair(Fq(Q, a), Fq(Q, b)))
    constructor(Q: BigInteger, a: BigInteger, b: BigInteger) : this(Q, Pair(Fq(Q, a), Fq(Q, b)))

    override val root: Fq
        get() = Fq(Q, -1)

    override fun companion(): FieldCompanion {
        return Companion
    }

    override val embedding: Int
        get() = 2
    val a get() = pair.first
    val b get() = pair.second

    override fun fromArgs(Q: BigInteger, args: List<Field>): Fq2 {
        return Fq2(Q, args[0] as Fq, args[1] as Fq)
    }

    override val extension: Int
        get() = 2

    override val sequence: Sequence<Fq>
        get() = pair.toList().asSequence()

    override fun invertImp(): Fq2 {
        val factor = (a * a + b * b).invertImp()
        return Fq2(Q, a * factor, -b * factor)
    }

    fun mulByNonresidue(): Fq2 {
        return Fq2(Q, a - b, a + b)
    }

    override fun modSqrtImp(): Fq2 {
        if (pair.second == Fq.zero(Q))
            return fromArgs(Q, listOf(pair.first.modSqrt(), Fq.zero(Q)))

        var alpha = pair.first.pow(2) + pair.second.pow(2)
        var gamma = alpha.pow((Q - 1) / 2)

        if (gamma == Fq(Q, -1))
            throw Exception("NO SQRT EXIST")
        alpha = alpha.modSqrt()

        var delta = (pair.first + alpha) * Fq(Q, 2).invertImp()

        gamma = delta.pow((Q - 1) / 2)
        if (gamma == Fq(Q, -1)) {
            delta = (pair.first - alpha) * Fq(Q, 2).invertImp()
        }
        val x0 = delta.modSqrt()
        val x1 = pair.second * (Fq(Q, 2) * x0).invertImp()
        return Fq2(Q, x0, x1)
    }

    companion object : FieldCompanion {
        override fun zero(Q: BigInteger): Fq2 {
            return fromFq(Q, Fq(Q, 0))
        }

        override fun one(Q: BigInteger): Fq2 {
            return fromFq(Q, Fq(Q, 1))
        }

        override fun fromFq(Q: BigInteger, fq: Fq): Fq2 {
            val y = base.fromFq(Q, fq) as Fq
            val z = base.zero(Q) as Fq
            return Fq2(Q, Pair(y, z))
        }

        override val base: FieldCompanion
            get() = Fq

        override fun isInstance(any: Any): Boolean {
            return Fq2::class.isInstance(any)
        }

        fun fromBytes(buffer: ByteArray, q: BigInteger): Fq2 {
            if (buffer.size != 2 * 48) {
                throw Exception("Invalid size")
            }

            // note these are reversed
            return Fq2(
                q, Pair(
                    Fq.fromBytes(buffer.drop(48).toByteArray(), q),
                    Fq.fromBytes(buffer.take(48).toByteArray(), q)

                )
            )

        }
    }
}

class Fq6(override val Q: BigInteger, private val triple: Triple<Fq2, Fq2, Fq2>) : FieldExt(Q) {
    val a get() = triple.first
    val b get() = triple.second
    val c get() = triple.third

    constructor(Q: BigInteger, a: Fq2, b: Fq2, c: Fq2) : this(Q, Triple(a, b, c))

    override val root: Fq2
        get() = Fq2(Q, Fq.one(Q), Fq.one(Q))
    override val extension: Int
        get() = 6
    override val embedding: Int
        get() = 3
    override val sequence: Sequence<Fq2>
        get() = triple.toList().asSequence()

    override fun fromArgs(Q: BigInteger, args: List<Field>): Fq6 {
        return Fq6(Q, args[0] as Fq2, args[1] as Fq2, args[2] as Fq2)
    }

    override fun companion(): FieldCompanion {
        return Companion
    }

    override fun invertImp(): Fq6 {
        val g0 = a * a - b * c.mulByNonresidue()
        val g1 = (c * c).mulByNonresidue() - a * b
        val g2 = b * b - a * c
        val factor = (g0 * a + (g1 * c + g2 * b).mulByNonresidue()).invertImp()
        return Fq6(Q, (g0 * factor), (g1 * factor), (g2 * factor))
    }

    override fun modSqrtImp(): Fq6 {
        TODO("Not yet implemented For Fq6")
    }

    fun mulByNonresidue(): Fq6 {
        return Fq6(Q, (c * root), a, b)
    }

    companion object : FieldCompanion {
        override fun zero(Q: BigInteger): Fq6 {
            return fromFq(Q, Fq(Q, 0))
        }

        override fun one(Q: BigInteger): Fq6 {
            return fromFq(Q, Fq(Q, 1))
        }

        override fun fromFq(Q: BigInteger, fq: Fq): Fq6 {
            val y = base.fromFq(Q, fq) as Fq2
            val z = base.zero(Q) as Fq2
            return Fq6(Q, Triple(y, z, z))
        }

        override val base: FieldCompanion
            get() = Fq2

        override fun isInstance(any: Any): Boolean {
            return Fq6::class.isInstance(any)
        }
    }
}

class Fq12(override val Q: BigInteger, val pair: Pair<Fq6, Fq6>) : FieldExt(Q) {
    constructor(Q: BigInteger, a: Fq6, b: Fq6) : this(Q, Pair(a, b))

    override val extension: Int
        get() = 12
    override val embedding: Int
        get() = 2
    override val root: Fq6
        get() = Fq6(Q, Fq2.zero(Q), Fq2.one(Q), Fq2.zero(Q))
    override val sequence: Sequence<Fq6>
        get() = pair.toList().asSequence()

    override fun companion(): FieldCompanion {
        return Companion
    }

    override fun invertImp(): Fq12 {
        val factor = (a * a - (b * b).mulByNonresidue()).invertImp()
        return Fq12(Q, (a * factor), ((-b).timesImp(factor)) as Fq6)
    }

    val a get() = pair.first
    val b get() = pair.second

    override fun modSqrtImp(): Fq12 {
        TODO("Not yet implemented For Fq12")
    }

    override fun fromArgs(Q: BigInteger, args: List<Field>): Fq12 {
        return Fq12(Q, Pair(args[0] as Fq6, args[1] as Fq6))
    }

    companion object : FieldCompanion {
        override fun zero(Q: BigInteger): Fq12 {
            return fromFq(Q, Fq(Q, 0))
        }

        override fun one(Q: BigInteger): Fq12 {
            return fromFq(Q, Fq(Q, 1))
        }

        override val base: FieldCompanion
            get() = Fq6

        override fun fromFq(Q: BigInteger, fq: Fq): Fq12 {
            val y = base.fromFq(Q, fq) as Fq6
            val z = base.zero(Q) as Fq6
            return Fq12(Q, Pair(y, z))
        }

        override fun isInstance(any: Any): Boolean {
            return Fq12::class.isInstance(any)
        }
    }
}


fun getCompanionFromClass(c: KClass<out Field>): FieldCompanion {
    if (c == Fq::class) {
        return Fq
    } else if (c == Fq2::class) {
        return Fq2
    } else if (c == Fq6::class) {
        return Fq6
    } else if (c == Fq12::class) {
        return Fq12
    } else {
        throw Exception()
    }
}
