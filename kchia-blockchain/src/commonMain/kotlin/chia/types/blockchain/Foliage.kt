package chia.types.blockchain

import bls.G2Element

data class TransactionInfo(
    val generatorRoot: UByteArray, // sha 256 of block generator for this block
    val generatorRefsRoot: UByteArray, // sha256 of the concatenation of the generator ref list entties
    val aggregatedSignature: G2Element,
    val fees: ULong, // user fees not block rewards
    val cost: ULong, // Cost of running in clvm
    val rewardClaimsIncorporated: List<Coin>
)

// Information that goes along with each transaction block that is relevant for light clients
data class FoliageTransactionBlock(
    val prevTransactionBlockhash: UByteArray,
    val timestamp: ULong,
    val filterHash: UByteArray,
    val additionsRoot: UByteArray,
    val removalsRoot: UByteArray,
    val transactionInfOHash: UByteArray
)

// part of block signed by plot key
data class FoliageBlockData(
    val unfinishedRewardBlockHash: UByteArray,
    val poolTarget: PoolTarget,
    val poolSiganture: G2Element?, // iff proofofspace has a pool pk
    val farmerRewardPuzzleHash: UByteArray,
    val extensionData: UByteArray
)

/**
 *  The entire foliage block, containing signature and the unsigned back pointer
 *  The hash of this is the "header hash". Note that for unfinished blocks, the prev_block_hash
 *  Is the prev from the signage point, and can be replaced with a more recent block
 */
data class Foliage(
    val prevBlockHash: UByteArray,
    val rewardBlockHash: UByteArray,
    val foliageBlockData: FoliageBlockData,
    val foliageBlockDataSignature: UByteArray,
    val foliageTransactionBlockHash: UByteArray?,
    val foliageTransactionblockSignature: G2Element?
)
