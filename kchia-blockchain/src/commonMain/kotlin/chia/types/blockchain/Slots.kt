package chia.types.blockchain

import bls.G2Element
import chia.types.serializers.G2ElementSerializer
import chia.types.serializers.UByteArraySerializer
import kotlinx.serialization.Serializable

@Serializable
data class ChallengeBlockInfo(
    val proofOfSpace: ProofOfSpace,
    val challengeChainSpVDFInfo: VDFInfo?,
    @Serializable(with = G2ElementSerializer::class)
    val challengeChainSpSignature: G2Element,
    val challengeChainIpVdf: VDFInfo
)

@Serializable
data class ChallengeChainSubSlot(
    val challengeChainEndOfSlotVdf :VDFInfo,
    @Serializable(with = UByteArraySerializer::class)
    val infusedChallengeChainSubSlotHash: UByteArray?,
    @Serializable(with = UByteArraySerializer::class)
    val subepochSummaryhash: UByteArray?,
    val newSubSlotIters: ULong?,
    val newDifficulty: ULong?
)

@Serializable
data class InfusedChallengeChainSubSlot(
    val infusedChallengeChainEndOfSlotVdf: VDFInfo
)

@Serializable
data class RewardChainSubSlot(
    val endOfSlotVdf: VDFInfo,
    @Serializable(with = UByteArraySerializer::class)
    val challengeChainSubSlotHash: UByteArray,
    @Serializable(with = UByteArraySerializer::class)
    val infusedChallengeChainSubSlotHash: UByteArray?,
    val deficit: UByte
)

@Serializable
data class SubSlotProofs(
    val challengeChainSlotProof: VDFProof,
    val infusedChallegneChainSlotProof: VDFProof?,
    val rewardChainSlotProof: VDFProof
)
