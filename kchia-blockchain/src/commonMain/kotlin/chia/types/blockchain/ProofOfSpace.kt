@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.types.blockchain

import bls.G1Element
import chia.Consensus
import chia.types.serializers.G1ElementSerializer
import chia.types.serializers.UByteArraySerializer
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import util.crypto.Sha256

@Serializable
class ProofOfSpace private constructor(
    @SerialName("challenge")
    @Serializable(with = UByteArraySerializer::class)
    val challenge: UByteArray,
    @Serializable(with = G1ElementSerializer::class)
    @SerialName("pool_public_key")
    val poolPublicKey: G1Element?, // one or other should be non null
    @SerialName("pool_contract_puzzle_hash")
    @Serializable(with = UByteArraySerializer::class)
    val poolContractPuzzleHash: UByteArray?,
    @Serializable(with = G1ElementSerializer::class)
    @SerialName("plot_public_key")
    val plotPublicKey: G1Element,
    @SerialName("size")
    val size: Int,
    @SerialName("proof")
    @Serializable(with = UByteArraySerializer::class)
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