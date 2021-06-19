package chia.types.blockchain

import bls.G2Element
import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.G2ElementSerializer
import chia.types.serializers.UByteArraySerializer
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Serializable

@Serializable
data class RewardChainBlockUnfinished(
    @Serializable(with = BigIntegerAsStringSerializer::class)
    val totalIters: BigInteger, // 128 bit limit
    val signagePointIndex: UByte,
    @Serializable(with = UByteArraySerializer::class)
    val posSsCcChallengeHash: UByteArray,
    val proofOfSpace: ProofOfSpace,
    val challengeChainSpVdf: VDFInfo?,
    @Serializable(with = G2ElementSerializer::class)
    val challengeChainSpSignature: G2Element,
    val rewardChainSpVdf: VDFInfo?,
    @Serializable(with = G2ElementSerializer::class)
    val rewardChainSpSignature: G2Element
)

@Serializable
data class RewardChainBlock(
    @Serializable(with = BigIntegerAsStringSerializer::class)
    val weight: BigInteger, //128 bit limit
    val height: UInt,
    @Serializable(with = BigIntegerAsStringSerializer::class)
    val totalIters: BigInteger,
    val signagePointIndex: UByte,
    @Serializable(with = UByteArraySerializer::class)
    val posSsCcChallengeHash: UByteArray,
    val proofOfSpace: ProofOfSpace,
    val challengeChainSpVdf: VDFInfo?,
    @Serializable(with = G2ElementSerializer::class)
    val challengeChainSpSignature: G2Element,
    val challengeChainIpVdf: VDFInfo,
    val rewardChainSpVdf: VDFInfo?,
    @Serializable(with = G2ElementSerializer::class)
    val rewardChainSpSignature: G2Element,
    val rewardChainIpVdf: VDFInfo,
    val infusedChallengeChainIpCdf: VDFInfo?, // iff deficit < 16
    val isTransactionBlock: Boolean
)
