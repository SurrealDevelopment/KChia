@file:Suppress("EXPERIMENTAL_API_USAGE")

package chia.types.blockchain

import bls.G1Element
import chia.Consensus
import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.G1ElementAsStringSerializer
import chia.types.serializers.UByteArrayAsStringSerializer
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import util.crypto.Sha256

@Serializable
class ProofOfSpace private constructor(
    @SerialName("challenge")
    @Contextual
    val challenge: Bytes32,
    @SerialName("pool_public_key")
    @Contextual
    val poolPublicKey: G1Element?, // one or other should be non null
    @SerialName("pool_contract_puzzle_hash")
    @Contextual
    val poolContractPuzzleHash: Bytes32?,
    @SerialName("plot_public_key")
    @Contextual
    val plotPublicKey: G1Element,
    @SerialName("size")
    val size: Int,
    @SerialName("proof")
    @Contextual
    val proof: UByteArray
) {
    // from pool key
    constructor( challenge: UByteArray,
                 poolPublicKey: G1Element,
                 plotPublicKey: G1Element,
                 size: Int,
                 proof: UByteArray) : this(challenge.asBytes32(), poolPublicKey,null,plotPublicKey,size,proof)

    // from puzzle hash
    constructor( challenge: UByteArray,
                 poolContractPuzzleHash: UByteArray?,
                 plotPublicKey: G1Element,
                 size: Int,
                 proof: UByteArray) : this(challenge.asBytes32(), null,
        poolContractPuzzleHash?.asBytes32(),plotPublicKey,size,proof)


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