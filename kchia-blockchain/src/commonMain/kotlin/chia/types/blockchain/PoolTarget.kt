package chia.types.blockchain

data class PoolTarget(
    val puzzleHash: UByteArray,
    val maxHeight: UInt // 0 is unlimited
)
