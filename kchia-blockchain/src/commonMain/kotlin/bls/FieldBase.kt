package bls

import com.ionspin.kotlin.bignum.integer.BigInteger

/**
 * Interface for Fq, Fq2, etc...
 * Note there is lots of boiler plate operation however
 * this keeps types safe across the code
 */
interface Field : Comparable<Field> {
    val extension: Int
    val Q: BigInteger

    infix fun plusImp(other: Field): Field
    infix fun plusImp(other: BigInteger): Field
    infix fun plusImp(other: Int): Field


    infix fun minusImp(other: Field): Field
    infix fun minusImp(other: BigInteger): Field
    infix fun minusImp(other: Int): Field

    fun unaryMinusImp(): Field

    infix fun timesImp(other: Field): Field
    infix fun timesImp(other: BigInteger): Field
    infix fun timesImp(other: Int): Field


    infix fun divImp(other: Field): Field
    infix fun divImp(other: BigInteger): Field
    infix fun divImp(other: Int): Field

    fun powImp(n: Int): Field
    fun powImp(n: BigInteger): Field

    fun qiPowerImp(n: Int): Field
    fun invertImp(): Field

    fun toByteArray(): ByteArray
    fun toUByteArray(): UByteArray

    fun modSqrtImp(): Field
    fun companion(): FieldCompanion

    // a sequence of the first underlying structure
    val sequence: Sequence<Field>

    // a sequence flattened to show the underlying
    val flatSequence: Sequence<Fq>
}


/**
 * Companion related to FieldBase which contains some much needed meta info about it.
 * @see Field.companion
 */
interface FieldCompanion {
    fun zero(Q: BigInteger): Field
    fun one(Q: BigInteger): Field
    fun fromFq(Q: BigInteger, fq: Fq): Field
    val base: FieldCompanion?
    fun isInstance(any: Any): Boolean
}
