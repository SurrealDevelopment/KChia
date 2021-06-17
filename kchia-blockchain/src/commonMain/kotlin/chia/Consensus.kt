package chia

import com.ionspin.kotlin.bignum.integer.BigInteger
import util.hexstring.asHexString

data class Consensus(
    val SLOT_BLOCKS_TARGET: UInt = 32u,
    val MIN_BLOCKS_PER_CHALLENGE_BLOCK: UInt = 16u,

    val MAX_SUB_SLOT_BLOCKS: UInt = 128u,
    val NUM_SPS_SUB_SLOT: UInt = 64u,  // The number of signage points per sub-slot (including the 0th sp at the sub-slot start)

    val SUB_SLOT_ITERS_STARTING: ULong = (2uL) shl 27,  // The sub_slot_iters for the first epoch
    val DIFFICULTY_CONSTANT_FACTOR: BigInteger = BigInteger(2).shl(67),  // Multiplied by the difficulty to get iterations
    val DIFFICULTY_STARTING: ULong = 7u, // The difficulty for the first epoch
    // The maximum factor by which difficulty and sub_slot_iters can change per epoch
    val DIFFICULTY_CHANGE_MAX_FACTOR: UInt = 3u,
    val SUB_EPOCH_BLOCKS: UInt = 384u,  // The number of blocks per sub-epoch
    val EPOCH_BLOCKS: UInt = 4608u,  // The number of blocks per sub-epoch, must be a multiple of SUB_EPOCH_BLOCKS

    val SIGNIFICANT_BITS: Int = 8,  // The number of bits to look at in difficulty and min iters. The rest are zeroed
    val DISCRIMINANT_SIZE_BITS: Int = 1024,  // Max is 1024 (based on ClassGroupElement int size)
    val NUMBER_ZERO_BITS_PLOT_FILTER: Int = 9,  // H(plot id + challenge hash + signage point) must start with these many zeroes
    val MIN_PLOT_SIZE: Int = 32,
    val MAX_PLOT_SIZE: Int = 50,
    val SUB_SLOT_TIME_TARGET: Int = 600,  // The target number of seconds per sub-slot
    val NUM_SP_INTERVALS_EXTRA: Int = 3,  // The difference between signage point and infusion point (plus required_iters)
    val MAX_FUTURE_TIME: Int = 5 * 60,  // The next block can have a timestamp of at most these many seconds more
    val NUMBER_OF_TIMESTAMPS: Int = 11,  // Than the average of the last NUMBER_OF_TIMESTAMPS blocks
    // Used as the initial cc rc challenges, as well as first block back pointers, and first SES back pointer
    // We override this value based on the chain being run (testnet0, testnet1, mainnet, etc)
    val GENESIS_CHALLENGE: UByteArray =
        "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855".asHexString().toUByteArray(),
    // Forks of chia should change this value to provide replay attack protection
    val AGG_SIG_ME_ADDITIONAL_DATA: UByteArray =
        "ccd5bb71183532bff220ba46c268991a3ff07eb358e8255a65c30a2dce0e5fbb".asHexString().toUByteArray(),
    val GENESIS_PRE_FARM_POOL_PUZZLE_HASH: UByteArray = // The block at height must pay out to this pool puzzle hash
        "d23da14695a188ae5708dd152263c4db883eb27edeb936178d4d988b8f3ce5fc".asHexString().toUByteArray(),
    val GENESIS_PRE_FARM_FARMER_PUZZLE_HASH: UByteArray = // The block at height must pay out to this farmer puzzle hash
        "3d8765d3a597ec1d99663f6c9816d915b9f68613ac94009884c4addaefcce6af".asHexString().toUByteArray(),
    val MAX_VDF_WITNESS_SIZE: Int = 64,  // The maximum number of classgroup elements within an n-wesolowski proof
    // Size of mempool = 10x the size of block
    val MEMPOOL_BLOCK_BUFFER: Int = 50,
    // Max coin amount uint(1 << 64). This allows coin amounts to fit in 64 bits. This is around 18M chia.
    val MAX_COIN_AMOUNT: ULong = ULong.MAX_VALUE, // (1 << 64) - 1
    // Max block cost in clvm cost units
    val MAX_BLOCK_COST_CLVM: ULong = 11000000000uL,
    // Cost per byte of generator program
    val COST_PER_BYTE: Int = 1200,

    val WEIGHT_PROOF_THRESHOLD: UInt = 2u,
    val WEIGHT_PROOF_RECENT_BLOCKS: UInt = 1000u,
    val MAX_BLOCK_COUNT_PER_REQUESTS: UInt = 32u,
    val INITIAL_FREEZE_END_TIMESTAMP: ULong = 1620061200uL, // Default Mon May 03 2021 17:00:00 GMT+0000
    val BLOCKS_CACHE_SIZE: UInt = 4608u + (128u * 4u),
    val NETWORK_TYPE: Int = 0,
    val MAX_GENERATOR_SIZE: UInt = 1000000u,
    val MAX_GENERATOR_REF_LIST_SIZE: UInt = 512u,
) {
    companion object {
        val MAINNET = Consensus()
    }
}