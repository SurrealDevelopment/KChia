@file:Suppress("EXPERIMENTAL_OVERRIDE", "EXPERIMENTAL_API_USAGE")

package chia.clisp_low_level.elements

enum class ClvmObjectType {
    ATOM,
    PAIR
}

interface AtomOrPair {
    val atom: UByteArray?
    val pair: Pair<AtomOrPair, AtomOrPair>?
    val atomOrPair: Any

    fun cons(right: AtomOrPair): AtomOrPair
}

/**
 * Clvm object is base component of Clisp.
 */
internal data class ClvmObject constructor(
    private val _atom: UByteArray?,
    private val _pair: Pair<AtomOrPair, AtomOrPair>?,
    internal val type: ClvmObjectType
) : AtomOrPair {

    constructor(atom: UByteArray) : this(atom, null, ClvmObjectType.ATOM)
    constructor(pair: Pair<AtomOrPair, AtomOrPair>) : this(null, pair, ClvmObjectType.PAIR)

    override val atom get() = if (type == ClvmObjectType.ATOM) _atom!! else null
    override val pair get() = if (type == ClvmObjectType.PAIR) _pair!! else null
    override val atomOrPair: Any
        get() = if (type == ClvmObjectType.ATOM) _atom!! else pair!!

    override fun cons(right: AtomOrPair): AtomOrPair = ClvmObject(Pair(this, right))

    companion object {
        internal fun cast(any: Any): AtomOrPair {
            if (any is Pair<*, *>) {
                if (any.first is AtomOrPair && any.second is AtomOrPair) {
                    @Suppress("UNCHECKED_CAST")
                    return ClvmObject(any as Pair<AtomOrPair, AtomOrPair>)
                }
            } else if (any is UByteArray) {
                return ClvmObject(any)
            } else if (any is AtomOrPair) {
                return any
            }
            throw IllegalStateException("Cannot cast: ${any::class.simpleName}")
        }
    }

}