@file:UseSerializers(BigIntegerAsStringSerializer::class, UByteArraySerializer::class, G1ElementSerializer::class)

package chia.types.blockchain

import bls.G2Element
import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.G1ElementSerializer
import chia.types.serializers.G2ElementSerializer
import chia.types.serializers.UByteArraySerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ChallengeBlockInfo(
    @SerialName("proof_of_space")
    val proofOfSpace: ProofOfSpace,
    @SerialName("challenge_chain_sp_vdf")
    val challengeChainSpVDFInfo: VDFInfo?,
    @SerialName("challenge_chain_sp_signature")
    val challengeChainSpSignature: G2Element,
    @SerialName("challenge_chain_ip_vdf")
    val challengeChainIpVdf: VDFInfo
)

@Serializable
data class ChallengeChainSubSlot(
    @SerialName("challenge_chain_end_of_slot_vdf")
    val challengeChainEndOfSlotVdf :VDFInfo,
    @SerialName("infused_challenge_chain_sub_slot_hash")
    val infusedChallengeChainSubSlotHash: UByteArray?,
    @SerialName("subepoch_summary_hash")
    val subepochSummaryhash: UByteArray?,
    @SerialName("new_sub_slot_iters")
    val newSubSlotIters: ULong?,
    @SerialName("new_difficulty")
    val newDifficulty: ULong?
)

@Serializable
data class InfusedChallengeChainSubSlot(
    @SerialName("infused_challenge_chain_end_of_slot_vdf")
    val infusedChallengeChainEndOfSlotVdf: VDFInfo
)

@Serializable
data class RewardChainSubSlot(
    @SerialName("end_of_slot_vdf")
    val endOfSlotVdf: VDFInfo,
    @SerialName("challenge_chain_sub_slot_hash")
    val challengeChainSubSlotHash: UByteArray,
    @SerialName("infused_challenge_chain_sub_slot_hash")
    val infusedChallengeChainSubSlotHash: UByteArray?,
    @SerialName("deficit")
    val deficit: UByte
)

@Serializable
data class SubSlotProofs(
    @SerialName("challenge_chain_slot_proof")
    val challengeChainSlotProof: VDFProof,
    @SerialName("infused_challenge_chain_slot_proof")
    val infusedChallegneChainSlotProof: VDFProof?,
    @SerialName("reward_chain_slot_proof")
    val rewardChainSlotProof: VDFProof
)
