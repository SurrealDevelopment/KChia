@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.types.blockchain

import bls.G1Element
import chia.Consensus
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import util.crypto.Sha256

class ProofOfSpace private constructor(
    val challenge: UByteArray,
    val poolPublicKey: G1Element?, // one or other should be non null
    val poolContractPuzzleHash: UByteArray?,
    val plotPublicKey: G1Element,
    val size: Int,
    val proof: UByteArray
) {
    // from pool key
    constructor( challenge: UByteArray,
                 poolPublicKey: G1Element,
                 plotPublicKey: G1Element,
                 size: Int,
                 proof: UByteArray) : this(challenge, poolPublicKey,null,plotPublicKey,size,proof)

    // from puzzle hash
    constructor( challenge: UByteArray,
                 poolContractPuzzleHash: UByteArray?,
                 plotPublicKey: G1Element,
                 size: Int,
                 proof: UByteArray) : this(challenge, null,poolContractPuzzleHash,plotPublicKey,size,proof)


    val plotId: UByteArray by lazy {
        if (poolPublicKey == null && poolContractPuzzleHash != null) {
            Sha256().digest(poolContractPuzzleHash + plotPublicKey.toUByteArray())
        } else if (poolPublicKey != null && poolContractPuzzleHash == null) {
            Sha256().digest(poolPublicKey.toUByteArray() + plotPublicKey.toUByteArray())
        } else
            throw IllegalStateException("Pool public key or pool contract puzzle hash must be define")
    }

    fun passesPlotFilter(consensus: Consensus, challengeHash: UByteArray, signatePoint: UByteArray): Boolean {
        val x = Sha256().digest(plotId + challengeHash + signatePoint)
        val numZeros = BigInteger.fromUByteArray(x, Sign.POSITIVE).shr(
            256 - consensus.NUMBER_ZERO_BITS_PLOT_FILTER
        )
        return numZeros == BigInteger.ZERO
    }
}