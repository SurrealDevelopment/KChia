package chia.types

import chia.types.blockchain.ChallengeChainSubSlot
import chia.types.blockchain.InfusedChallengeChainSubSlot
import chia.types.blockchain.RewardChainSubSlot
import chia.types.blockchain.SubSlotProofs

data class EndOfSubSlotBundle(
    val challengeChain: ChallengeChainSubSlot,
    val infusedChallengeChain: InfusedChallengeChainSubSlot?,
    val rewardChain: RewardChainSubSlot,
    val proofs: SubSlotProofs
)
