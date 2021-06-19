package chia.types

import chia.types.blockchain.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullBlock(
    @SerialName("finished_sub_slots")
    val finishedSubSlots: List<EndOfSubSlotBundle>,
    @SerialName("reward_chain_block")
    val rewardChainBlock: RewardChainBlock,
    @SerialName("challenge_chain_sp_proof")
    val challengeChainSpProof: VDFProof?,
    @SerialName("challenge_chain_ip_proof")
    val challengeChainIpProof: VDFProof,
    @SerialName("reward_chain_sp_proof")
    val rewardChainSpProof: VDFProof?,
    @SerialName("reward_chain_ip_proof")
    val rewardChainIpProof: VDFProof,
    @SerialName("infused_challenge_chain_ip_proof")
    val infusedChallengeChainIpProof: VDFProof?,
    @SerialName("foliage")
    val foliage: Foliage,
    @SerialName("foliage_transaction_block")
    val foliageTransactionBlock: FoliageTransactionBlock,
    @SerialName("transactions_info")
    val transactionInfo: TransactionInfo?,
    @SerialName("transactions_generator")
    val transactionGenerator: Program?,
    @SerialName("transactions_generator_ref_list")
    val transactioNRefList: List<UInt>
)
