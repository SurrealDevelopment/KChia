@file:Suppress("unused")

package bls

import com.ionspin.kotlin.bignum.integer.BigInteger

/**
 * Boiler plate to help some conventions and type casts
 */

// Generic Field
operator fun Field.times(other: BigInteger): Field = this.timesImp(other)
operator fun Field.times(other: Int): Field = this.timesImp(other)
operator fun Field.times(other: Field): Field = this.timesImp(other)
operator fun Field.times(other: Fq): Field = this.timesImp(other)
operator fun Field.times(other: Fq2): Field = this.timesImp(other)
operator fun Field.times(other: Fq6): Field = this.timesImp(other)
operator fun Field.times(other: Fq12): Field = this.timesImp(other)

operator fun Field.plus(other: BigInteger): Field = this.plusImp(other)
operator fun Field.plus(other: Int): Field = this.plusImp(other)
operator fun Field.plus(other: Field): Field = this.plusImp(other)
operator fun Field.plus(other: Fq): Field = this.plusImp(other)
operator fun Field.plus(other: Fq2): Field = this.plusImp(other)
operator fun Field.plus(other: Fq6): Field = this.plusImp(other)
operator fun Field.plus(other: Fq12): Field = this.plusImp(other)

operator fun Field.minus(other: BigInteger): Field = this.minusImp(other)
operator fun Field.minus(other: Int): Field = this.minusImp(other)
operator fun Field.minus(other: Field): Field = this.minusImp(other)
operator fun Field.minus(other: Fq): Field = this.minusImp(other)
operator fun Field.minus(other: Fq2): Field = this.minusImp(other)
operator fun Field.minus(other: Fq6): Field = this.minusImp(other)
operator fun Field.minus(other: Fq12): Field = this.minusImp(other)

operator fun Field.div(other: BigInteger): Field = this.divImp(other)
operator fun Field.div(other: Int): Field = this.divImp(other)
operator fun Field.div(other: Field): Field = this.divImp(other)
operator fun Field.div(other: Fq): Field = this.divImp(other)
operator fun Field.div(other: Fq2): Field = this.divImp(other)
operator fun Field.div(other: Fq6): Field = this.divImp(other)
operator fun Field.div(other: Fq12): Field = this.divImp(other)

fun Field.pow(other: Int): Field = this.powImp(other)
fun Field.pow(other: BigInteger): Field = this.powImp(other)
fun Field.qiPower(other: Int): Field = this.qiPowerImp(other)
fun Field.modSqrt(): Field = this.modSqrtImp()
operator fun Field.unaryMinus(): Field = this.unaryMinusImp()
operator fun Field.not(): Field = this.invertImp()

// Fq
operator fun Fq.times(other: BigInteger): Fq = this.timesImp(other)
operator fun Fq.times(other: Int): Fq = this.timesImp(other)
operator fun Fq.times(other: Fq): Fq = this.timesImp(other) as Fq
operator fun Fq.times(other: Fq2): Fq2 = this.timesImp(other) as Fq2
operator fun Fq.times(other: Fq6): Fq6 = this.timesImp(other) as Fq6
operator fun Fq.times(other: Fq12): Fq12 = this.timesImp(other) as Fq12

operator fun Fq.plus(other: BigInteger): Fq = this.plusImp(other)
operator fun Fq.plus(other: Int): Fq = this.plusImp(other)
operator fun Fq.plus(other: Fq): Fq = this.plusImp(other) as Fq
operator fun Fq.plus(other: Fq2): Fq2 = this.plusImp(other) as Fq2
operator fun Fq.plus(other: Fq6): Fq6 = this.plusImp(other) as Fq6
operator fun Fq.plus(other: Fq12): Fq12 = this.plusImp(other) as Fq12

operator fun Fq.minus(other: BigInteger): Fq = this.minusImp(other)
operator fun Fq.minus(other: Int): Fq = this.minusImp(other)
operator fun Fq.minus(other: Fq): Fq = this.minusImp(other) as Fq
operator fun Fq.minus(other: Fq2): Fq2 = this.minusImp(other) as Fq2
operator fun Fq.minus(other: Fq6): Fq6 = this.minusImp(other) as Fq6
operator fun Fq.minus(other: Fq12): Fq12 = this.minusImp(other) as Fq12

operator fun Fq.div(other: BigInteger): Fq = this.divImp(other)
operator fun Fq.div(other: Int): Fq = this.divImp(other)
operator fun Fq.div(other: Fq): Fq = this.divImp(other) as Fq
operator fun Fq.div(other: Fq2): Fq2 = this.divImp(other) as Fq2
operator fun Fq.div(other: Fq6): Fq6 = this.divImp(other) as Fq6
operator fun Fq.div(other: Fq12): Fq12 = this.divImp(other) as Fq12

fun Fq.pow(other: Int): Fq = this.powImp(other)
fun Fq.pow(other: BigInteger): Fq = this.powImp(other)
fun Fq.qiPower(other: Int): Fq = this.qiPowerImp(other)
fun Fq.modSqrt(): Fq = this.modSqrtImp()
operator fun Fq.unaryMinus(): Fq = this.unaryMinusImp()
operator fun Fq.not(): Fq = this.invertImp()


// Fq2
operator fun Fq2.times(other: BigInteger): Fq2 = this.timesImp(other) as Fq2
operator fun Fq2.times(other: Int): Fq2 = this.timesImp(other) as Fq2
operator fun Fq2.times(other: Fq): Fq2 = this.timesImp(other) as Fq2
operator fun Fq2.times(other: Fq2): Fq2 = this.timesImp(other) as Fq2
operator fun Fq2.times(other: Fq6): Fq6 = this.timesImp(other) as Fq6
operator fun Fq2.times(other: Fq12): Fq12 = this.timesImp(other) as Fq12

operator fun Fq2.minus(other: BigInteger): Fq2 = this.minusImp(other) as Fq2
operator fun Fq2.minus(other: Int): Fq2 = this.minusImp(other) as Fq2
operator fun Fq2.minus(other: Fq): Fq2 = this.minusImp(other) as Fq2
operator fun Fq2.minus(other: Fq2): Fq2 = this.minusImp(other) as Fq2
operator fun Fq2.minus(other: Fq6): Fq6 = this.minusImp(other) as Fq6
operator fun Fq2.minus(other: Fq12): Fq12 = this.minusImp(other) as Fq12

operator fun Fq2.plus(other: BigInteger): Fq2 = this.plusImp(other) as Fq2
operator fun Fq2.plus(other: Int): Fq2 = this.plusImp(other) as Fq2
operator fun Fq2.plus(other: Fq): Fq2 = this.plusImp(other) as Fq2
operator fun Fq2.plus(other: Fq2): Fq2 = this.plusImp(other) as Fq2
operator fun Fq2.plus(other: Fq6): Fq6 = this.plusImp(other) as Fq6
operator fun Fq2.plus(other: Fq12): Fq12 = this.plusImp(other) as Fq12

operator fun Fq2.div(other: BigInteger): Fq2 = this.divImp(other) as Fq2
operator fun Fq2.div(other: Int): Fq2 = this.divImp(other) as Fq2
operator fun Fq2.div(other: Fq): Fq2 = this.divImp(other) as Fq2
operator fun Fq2.div(other: Fq2): Fq2 = this.divImp(other) as Fq2
operator fun Fq2.div(other: Fq6): Fq6 = this.divImp(other) as Fq6
operator fun Fq2.div(other: Fq12): Fq12 = this.divImp(other) as Fq12

fun Fq2.pow(other: Int): Fq2 = this.powImp(other) as Fq2
fun Fq2.pow(other: BigInteger): Fq2 = this.powImp(other) as Fq2
fun Fq2.qiPower(other: Int): Fq2 = this.qiPowerImp(other) as Fq2
fun Fq2.modSqrt(): Fq2 = this.modSqrtImp()
operator fun Fq2.unaryMinus(): Fq2 = this.unaryMinusImp() as Fq2
operator fun Fq2.not(): Fq2 = this.invertImp()

// Fq6
operator fun Fq6.times(other: BigInteger): Fq6 = this.timesImp(other) as Fq6
operator fun Fq6.times(other: Int): Fq6 = this.timesImp(other) as Fq6
operator fun Fq6.times(other: Fq): Fq6 = this.timesImp(other) as Fq6
operator fun Fq6.times(other: Fq2): Fq6 = this.timesImp(other) as Fq6
operator fun Fq6.times(other: Fq6): Fq6 = this.timesImp(other) as Fq6
operator fun Fq6.times(other: Fq12): Fq12 = this.timesImp(other) as Fq12

operator fun Fq6.plus(other: BigInteger): Fq6 = this.plusImp(other) as Fq6
operator fun Fq6.plus(other: Int): Fq6 = this.plusImp(other) as Fq6
operator fun Fq6.plus(other: Fq): Fq6 = this.plusImp(other) as Fq6
operator fun Fq6.plus(other: Fq2): Fq6 = this.plusImp(other) as Fq6
operator fun Fq6.plus(other: Fq6): Fq6 = this.plusImp(other) as Fq6
operator fun Fq6.plus(other: Fq12): Fq12 = this.plusImp(other) as Fq12

operator fun Fq6.minus(other: BigInteger): Fq6 = this.minusImp(other) as Fq6
operator fun Fq6.minus(other: Int): Fq6 = this.minusImp(other) as Fq6
operator fun Fq6.minus(other: Fq): Fq6 = this.minusImp(other) as Fq6
operator fun Fq6.minus(other: Fq2): Fq6 = this.minusImp(other) as Fq6
operator fun Fq6.minus(other: Fq6): Fq6 = this.minusImp(other) as Fq6
operator fun Fq6.minus(other: Fq12): Fq12 = this.minusImp(other) as Fq12

operator fun Fq6.div(other: BigInteger): Fq6 = this.divImp(other) as Fq6
operator fun Fq6.div(other: Int): Fq6 = this.divImp(other) as Fq6
operator fun Fq6.div(other: Fq): Fq6 = this.divImp(other) as Fq6
operator fun Fq6.div(other: Fq2): Fq6 = this.divImp(other) as Fq6
operator fun Fq6.div(other: Fq6): Fq6 = this.divImp(other) as Fq6
operator fun Fq6.div(other: Fq12): Fq12 = this.divImp(other) as Fq12

fun Fq6.pow(other: Int): Fq6 = this.powImp(other) as Fq6
fun Fq6.pow(other: BigInteger): Fq6 = this.powImp(other) as Fq6
fun Fq6.qiPower(other: Int): Fq6 = this.qiPowerImp(other) as Fq6
fun Fq6.modSqrt(): Fq6 = this.modSqrtImp()
operator fun Fq6.unaryMinus(): Fq6 = this.unaryMinusImp() as Fq6
operator fun Fq6.not(): Fq6 = this.invertImp()

// Fq 12
operator fun Fq12.times(other: BigInteger): Fq12 = this.timesImp(other) as Fq12
operator fun Fq12.times(other: Int): Fq12 = this.timesImp(other) as Fq12
operator fun Fq12.times(other: Fq): Fq12 = this.timesImp(other) as Fq12
operator fun Fq12.times(other: Fq2): Fq12 = this.timesImp(other) as Fq12
operator fun Fq12.times(other: Fq6): Fq12 = this.timesImp(other) as Fq12
operator fun Fq12.times(other: Fq12): Fq12 = this.timesImp(other) as Fq12

operator fun Fq12.plus(other: BigInteger): Fq12 = this.plusImp(other) as Fq12
operator fun Fq12.plus(other: Int): Fq12 = this.plusImp(other) as Fq12
operator fun Fq12.plus(other: Fq): Fq12 = this.plusImp(other) as Fq12
operator fun Fq12.plus(other: Fq2): Fq12 = this.plusImp(other) as Fq12
operator fun Fq12.plus(other: Fq6): Fq12 = this.plusImp(other) as Fq12
operator fun Fq12.plus(other: Fq12): Fq12 = this.plusImp(other) as Fq12

operator fun Fq12.minus(other: BigInteger): Fq12 = this.minusImp(other) as Fq12
operator fun Fq12.minus(other: Int): Fq12 = this.minusImp(other) as Fq12
operator fun Fq12.minus(other: Fq): Fq12 = this.minusImp(other) as Fq12
operator fun Fq12.minus(other: Fq2): Fq12 = this.minusImp(other) as Fq12
operator fun Fq12.minus(other: Fq6): Fq12 = this.minusImp(other) as Fq12
operator fun Fq12.minus(other: Fq12): Fq12 = this.minusImp(other) as Fq12

operator fun Fq12.div(other: BigInteger): Fq12 = this.divImp(other) as Fq12
operator fun Fq12.div(other: Int): Fq12 = this.divImp(other) as Fq12
operator fun Fq12.div(other: Fq): Fq12 = this.divImp(other) as Fq12
operator fun Fq12.div(other: Fq2): Fq12 = this.divImp(other) as Fq12
operator fun Fq12.div(other: Fq6): Fq12 = this.divImp(other) as Fq12
operator fun Fq12.div(other: Fq12): Fq12 = this.divImp(other) as Fq12

fun Fq12.pow(other: Int): Fq12 = this.powImp(other) as Fq12
fun Fq12.pow(other: BigInteger): Fq12 = this.powImp(other) as Fq12
fun Fq12.qiPower(other: Int): Fq12 = this.qiPowerImp(other) as Fq12
fun Fq12.modSqrt(): Fq12 = this.modSqrtImp()
operator fun Fq12.unaryMinus(): Fq12 = this.unaryMinusImp() as Fq12
operator fun Fq12.not(): Fq12 = this.invertImp()