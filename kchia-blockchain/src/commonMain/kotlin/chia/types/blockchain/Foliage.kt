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
data class TransactionInfo(
    @SerialName("generator_root")
    val generatorRoot: UByteArray, // sha 256 of block generator for this block
    @SerialName("generator_refs_root")
    val generatorRefsRoot: UByteArray, // sha256 of the concatenation of the generator ref list entties
    @SerialName("aggregated_signature")
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
    val prevTransactionBlockhash: UByteArray,
    @SerialName("timestamp")
    val timestamp: ULong,
    @SerialName("filter_hash")
    val filterHash: UByteArray,
    @SerialName("additions_root")
    val additionsRoot: UByteArray,
    @SerialName("removals_root")
    val removalsRoot: UByteArray,
    @SerialName("transactions_info_hash")
    val transactionInfOHash: UByteArray
)

// part of block signed by plot key
@Serializable
data class FoliageBlockData(
    @SerialName("unfinished_reward_block_hash")
    val unfinishedRewardBlockHash: UByteArray,
    @SerialName("pool_target")
    val poolTarget: PoolTarget,
    @SerialName("pool_signature")
    val poolSiganture: G2Element?, // iff proofofspace has a pool pk
    @SerialName("farmer_reward_puzzle_hash")
    val farmerRewardPuzzleHash: UByteArray,
    @SerialName("extension_data")
    val extensionData: UByteArray
)

/**
 *  The entire foliage block, containing signature and the unsigned back pointer
 *  The hash of this is the "header hash". Note that for unfinished blocks, the prev_block_hash
 *  Is the prev from the signage point, and can be replaced with a more recent block
 */
@Serializable
data class Foliage(
    @SerialName("prev_block_hash")
    val prevBlockHash: UByteArray,
    @SerialName("reward_block_hash")
    val rewardBlockHash: UByteArray,
    @SerialName("foliage_block_data")
    val foliageBlockData: FoliageBlockData,
    @SerialName("foliage_block_data_signature")
    val foliageBlockDataSignature: UByteArray,
    @SerialName("foliage_transaction_block_hash")
    val foliageTransactionBlockHash: UByteArray?,
    @SerialName("foliage_transaction_block_signature")
    val foliageTransactionblockSignature: G2Element?
)
