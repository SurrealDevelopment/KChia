package chia.types

import chia.types.blockchain.ChallengeChainSubSlot
import chia.types.blockchain.InfusedChallengeChainSubSlot
import chia.types.blockchain.RewardChainSubSlot
import chia.types.blockchain.SubSlotProofs
import chia.types.serializers.UByteArraySerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EndOfSubSlotBundle(
    @SerialName("challenge_chain")
    val challengeChain: ChallengeChainSubSlot,
    @SerialName("infused_challenge_chain")
    val infusedChallengeChain: InfusedChallengeChainSubSlot?,
    @SerialName("reward_chain")
    val rewardChain: RewardChainSubSlot,
    @SerialName("proofs")
    val proofs: SubSlotProofs
)
