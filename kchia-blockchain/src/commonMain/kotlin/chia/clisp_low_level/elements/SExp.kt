@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_OVERRIDE")

package chia.clisp_low_level.elements

import bls.G1Element
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.util.fromTwosComplementByteArray
import kotlinx.serialization.Serializable
import util.extensions.*
import util.extensions.toBytes
import util.hexstring.toHexString


@ExperimentalUnsignedTypes
@Suppress("unused", "MemberVisibilityCanBePrivate", "ObjectPropertyName")
internal fun SExp.Companion.toImp(from: Any?): SExp {

    if (from is SExp) return from

    var v: Any? = from
    val stack = ArrayList(listOf(v))
    val ops: ArrayList<Pair<Int, Int?>> = ArrayList()
    ops.push(Pair(0, null))

    while (ops.size > 0) {
        val p = ops.pop()
        val op = p.first
        when (op) {
            0 -> {
                v = stack.pop()
                when {
                    v is Pair<*, *> -> {
                        val left = v.first
                        val right = v.second
                        val target = stack.size
                        stack.push(Pair(left, right))
                        if (right !is AtomOrPair) {
                            stack.push(right)
                            ops.push(Pair(2, target))
                            ops.push(Pair(0, null))
                        }
                        if (left !is AtomOrPair) {
                            stack.push(left)
                            ops.push(Pair(1, target))
                            ops.push(Pair(0, null))
                        }
                    }
                    v is AtomOrPair -> {
                        stack.push(v.atomOrPair)
                    }
                    v is UByteArray -> {
                        stack.push(v)
                    }
                    v is ByteArray -> {
                        stack.push(v.toUByteArray())
                    }
                    v is G1Element -> {
                        stack.push(v.toUByteArray())
                    }
                    v is Int -> {
                        stack.push(BigInteger(v).toTrimmed2sCompUbyteARray())
                    }
                    v is UInt -> {
                        stack.push(v.toBytes().toUByteArray().trimSigned())
                    }
                    v is Long -> {
                        stack.push(BigInteger(v).toTrimmed2sCompUbyteARray())
                    }
                    v is ULong -> {
                        stack.push(v.toBytes().toUByteArray().trimSigned())
                    }
                    v is String -> {
                        stack.push(v.encodeToByteArray().toUByteArray()) // utf8
                    }
                    v == null -> {
                        stack.push(__null__)
                    }
                    v is BigInteger -> {
                        stack.push(v.toTrimmed2sCompUbyteARray())
                    }
                    v is Collection<*> -> {
                        val target = stack.size
                        stack.push(__null__)
                        (v as Iterable<*>).forEach {
                            stack.push(it)
                            ops.push(Pair(3, target))
                            if (it !is AtomOrPair) {
                                ops.push(Pair(0, null))
                            }
                        }
                    }
                    else -> {
                        throw IllegalArgumentException("SEXP does not support $v (${v::class})")
                    }

                }
            }
            1 -> {
                val target = p.second!!
                stack[target] = Pair(
                    ClvmObject.cast(stack.pop()!!),
                    (stack[target] as Pair<*, *>).second
                )
            }
            2 -> {
                val target = p.second!!
                stack[target] = Pair(
                    (stack[target] as Pair<*, *>).first,
                    ClvmObject.cast(stack.pop()!!),
                )
            }
            3 -> { // prepend
                val target = p.second!!
                val tmp = stack[target]
                stack[target] = Pair(
                    ClvmObject.cast(stack.pop()!!),
                    ClvmObject.cast(tmp!!)
                )
            }
        } // while (ops)

    }
    val result = stack[0]
    if (result == null || stack.size != 1) {
        if (result == null) {
            throw IllegalStateException("internal error: Null")
        } else {
            throw IllegalStateException("internal error: ${result::class.simpleName}")
        }
    }
    return SExp(ClvmObject.cast(result))

}

@Suppress("unused", "MemberVisibilityCanBePrivate", "ObjectPropertyName")
open class SExp internal constructor(internal val _object: AtomOrPair) :
    Sequence<SExp>,
    AtomOrPair{

    constructor(pair: Pair<AtomOrPair, AtomOrPair>) : this(ClvmObject(pair))
    constructor(atom: UByteArray) : this(ClvmObject(atom))

    override val atom: UByteArray?
        get() = _object.atom
    override val atomOrPair: Any
        get() = _object.atomOrPair
    override val pair: Pair<AtomOrPair, AtomOrPair>?
        get() = _object.pair

    override fun cons(right: AtomOrPair): SExp = SExp(_object.cons(right))

    companion object {

        val __null__: SExp = SExp(UByteArray(0))
        val __true__ = SExp(UByteArray(1) {
            (1u).toUByte()
        })
        val __false__ = __null__

        infix fun to(that: SExp) = toImp(that)
        infix fun to(that: Pair<Any?, Any?>) = toImp(that)
        infix fun to(that: AtomOrPair) = toImp(that)
        infix fun to(that: ByteArray) = toImp(that)
        infix fun to(that: Int) = toImp(that)
        infix fun to(that: Long) = toImp(that)
        infix fun to(that: UInt) = toImp(that)
        infix fun to(that: ULong) = toImp(that)
        infix fun to(that: G1Element) = toImp(that)
        infix fun to(that: BigInteger) = toImp(that)

        infix fun to(that: String) = toImp(that)
        infix fun to(that: Iterable<Any>) = toImp(that)

    }

    val asByteArraySequence: Sequence<UByteArray> get() = SExpSerailization.SexpByteSequence(this)

    val serialized: UByteArray
        get() {
            return SExpSerailization.SexpByteSequence(this).flatten().toList().toUByteArray()
        }

    val hex: String
        get() {
            val bytes = asByteArraySequence.flatten().toList().toUByteArray()
            return bytes.toHexString()
        }

    val treeHash: UByteArray
        get() {
            return sha256TreeHash(this)
        }

    // assume this atom is a utf 8 string
    fun asString(): String {
        return atom!!.toByteArray().decodeToString()
    }

    private val sequence = sequence {
        var v = this@SExp
        while (!v.nullp()) {
            yield(v.first())
            v = v.rest()
        }
    }

    override fun iterator(): Iterator<SExp> {
        return sequence.iterator()
    }

    override fun equals(other: Any?): Boolean {
        val test = try {
            SExp.toImp(other)
        } catch (e :Exception) {
            return false
        }
        val compareStack = ArrayList<Pair<AtomOrPair, AtomOrPair>>()
        compareStack.push(Pair(this, test))
        while (compareStack.size > 0) {
            val pair = compareStack.pop()
            val p1 = pair.first.pair
            if (p1 != null) {
                val p2 = pair.second.pair
                if (p2 != null) {
                    compareStack.push(Pair(p1.first, p2.first))
                    compareStack.push(Pair(p1.second, p2.second))
                } else {
                    return false
                }
            } else if (pair.second.pair != null || !pair.first.atom!!.contentEquals(pair.second.atom!!) ) {
                return false
            }
        }
        return true

    }

    fun asBig(): BigInteger {
        return BigInteger.fromTwosComplementByteArray(this.atom!!.toByteArray())
    }
    override fun toString(): String {
        return "SExp($hex)"
    }

    override fun hashCode(): Int {
        return treeHash.hashCode()
    }

}

