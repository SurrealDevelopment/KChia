package chia.types.blockchain

import bls.G2Element
import chia.types.serializers.G2ElementSerializer
import chia.types.serializers.UByteArraySerializer
import kotlinx.serialization.Serializable

@Serializable
data class TransactionInfo(
    @Serializable(with = UByteArraySerializer::class)
    val generatorRoot: UByteArray, // sha 256 of block generator for this block
    @Serializable(with = UByteArraySerializer::class)
    val generatorRefsRoot: UByteArray, // sha256 of the concatenation of the generator ref list entties
    @Serializable(with = G2ElementSerializer::class)
    val aggregatedSignature: G2Element,
    val fees: ULong, // user fees not block rewards
    val cost: ULong, // Cost of running in clvm
    val rewardClaimsIncorporated: List<Coin>
)

// Information that goes along with each transaction block that is relevant for light clients
@Serializable
data class FoliageTransactionBlock(
    @Serializable(with = UByteArraySerializer::class)
    val prevTransactionBlockhash: UByteArray,
    val timestamp: ULong,
    @Serializable(with = UByteArraySerializer::class)
    val filterHash: UByteArray,
    @Serializable(with = UByteArraySerializer::class)
    val additionsRoot: UByteArray,
    @Serializable(with = UByteArraySerializer::class)
    val removalsRoot: UByteArray,
    @Serializable(with = UByteArraySerializer::class)
    val transactionInfOHash: UByteArray
)

// part of block signed by plot key
@Serializable
data class FoliageBlockData(
    @Serializable(with = UByteArraySerializer::class)
    val unfinishedRewardBlockHash: UByteArray,
    val poolTarget: PoolTarget,
    @Serializable(with = G2ElementSerializer::class)
    val poolSiganture: G2Element?, // iff proofofspace has a pool pk
    @Serializable(with = UByteArraySerializer::class)
    val farmerRewardPuzzleHash: UByteArray,
    @Serializable(with = UByteArraySerializer::class)
    val extensionData: UByteArray
)

/**
 *  The entire foliage block, containing signature and the unsigned back pointer
 *  The hash of this is the "header hash". Note that for unfinished blocks, the prev_block_hash
 *  Is the prev from the signage point, and can be replaced with a more recent block
 */
@Serializable
data class Foliage(
    @Serializable(with = UByteArraySerializer::class)
    val prevBlockHash: UByteArray,
    @Serializable(with = UByteArraySerializer::class)
    val rewardBlockHash: UByteArray,
    val foliageBlockData: FoliageBlockData,
    @Serializable(with = UByteArraySerializer::class)
    val foliageBlockDataSignature: UByteArray,
    @Serializable(with = UByteArraySerializer::class)
    val foliageTransactionBlockHash: UByteArray?,
    @Serializable(with = G2ElementSerializer::class)
    val foliageTransactionblockSignature: G2Element?
)
