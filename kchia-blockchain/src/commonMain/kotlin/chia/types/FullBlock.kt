package chia.types

import chia.types.blockchain.*

data class FullBlock(
    val finishedSubSlots: List<EndOfSubSlotBundle>,
    val rewardChainBlock: RewardChainBlock,
    val challengeChainSpProof: VDFProof?,
    val challengeChainIpProof: VDFProof,
    val rewardChainSpProof: VDFProof?,
    val rewardChainIpProof: VDFProof,
    val infusedChallengeChainIpProof: VDFProof?,
    val foliage: Foliage,
    val foliageTransactionBlock: FoliageTransactionBlock,
    val transactionInfo: TransactionInfo?,
    val transactionGenerator: Program?,
    val transactioNRefList: List<UInt>
)
