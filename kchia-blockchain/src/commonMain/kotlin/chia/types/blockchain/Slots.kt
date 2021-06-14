package chia.types.blockchain

import bls.G2Element

data class ChallengeBlockInfo(
    val proofOfSpace: ProofOfSpace,
    val challengeChainSpVDFInfo: VDFInfo?,
    val challengeChainSpSignature: G2Element,
    val challengeChainIpVdf: VDFInfo
)

data class ChallengeChainSubSlot(
    val challengeChainEndOfSlotVdf :VDFInfo,
    val infusedChallengeChainSubSlotHash: UByteArray?,
    val subepochSummaryhash: UByteArray?,
    val newSubSlotIters: ULong?,
    val newDifficulty: ULong?
)

data class InfusedChallengeChainSubSlot(
    val infusedChallengeChainEndOfSlotVdf: VDFInfo
)

data class RewardChainSubSlot(
    val endOfSlotVdf: VDFInfo,
    val challengeChainSubSlotHash: UByteArray,
    val infusedChallengeChainSubSlotHash: UByteArray?,
    val deficit: UByte
)

data class SubSlotProofs(
    val challengeChainSlotProof: VDFProof,
    val infusedChallegneChainSlotProof: VDFProof?,
    val rewardChainSlotProof: VDFProof
)
