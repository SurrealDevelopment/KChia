package chia.types.blockchain

import bls.G2Element
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionInfo(
    @SerialName("generator_root")
    @Contextual
    val generatorRoot: Bytes32, // sha 256 of block generator for this block
    @SerialName("generator_refs_root")
    @Contextual
    val generatorRefsRoot: Bytes32, // sha256 of the concatenation of the generator ref list entties
    @SerialName("aggregated_signature")
    @Contextual
    val aggregatedSignature: G2Element,
    @SerialName("fees")
    val fees: ULong, // user fees not block rewards
    @SerialName("cost")
    val cost: ULong, // Cost of running in clvm
    @SerialName("reward_claims_incorporated")
    val rewardClaimsIncorporated: List<Coin>
)

// Information that goes along with each transaction block that is relevant for light clients
@Serializable
data class FoliageTransactionBlock(
    @SerialName("prev_transaction_block_hash")
    @Contextual
    val prevTransactionBlockhash: Bytes32,
    @SerialName("timestamp")
    val timestamp: ULong,
    @SerialName("filter_hash")
    @Contextual
    val filterHash: Bytes32,
    @SerialName("additions_root")
    @Contextual
    val additionsRoot: Bytes32,
    @SerialName("removals_root")
    @Contextual
    val removalsRoot: Bytes32,
    @SerialName("transactions_info_hash")
    @Contextual
    val transactionInfOHash: Bytes32
)

// part of block signed by plot key
@Serializable
data class FoliageBlockData(
    @SerialName("unfinished_reward_block_hash")
    @Contextual
    val unfinishedRewardBlockHash: Bytes32,
    @SerialName("pool_target")
    val poolTarget: PoolTarget,
    @SerialName("pool_signature")
    @Contextual
    val poolSiganture: G2Element?, // iff proofofspace has a pool pk
    @SerialName("farmer_reward_puzzle_hash")
    @Contextual
    val farmerRewardPuzzleHash: Bytes32,
    @SerialName("extension_data")
    @Contextual
    val extensionData: Bytes32
)

/**
 *  The entire foliage block, containing signature and the unsigned back pointer
 *  The hash of this is the "header hash". Note that for unfinished blocks, the prev_block_hash
 *  Is the prev from the signage point, and can be replaced with a more recent block
 */
@Serializable
data class Foliage(
    @SerialName("prev_block_hash")
    @Contextual
    val prevBlockHash: Bytes32,
    @SerialName("reward_block_hash")
    @Contextual
    val rewardBlockHash: Bytes32,
    @SerialName("foliage_block_data")
    val foliageBlockData: FoliageBlockData,
    @SerialName("foliage_block_data_signature")
    @Contextual
    val foliageBlockDataSignature: G2Element,
    @SerialName("foliage_transaction_block_hash")
    @Contextual
    val foliageTransactionBlockHash: Bytes32?,
    @SerialName("foliage_transaction_block_signature")
    @Contextual
    val foliageTransactionblockSignature: G2Element?
)
