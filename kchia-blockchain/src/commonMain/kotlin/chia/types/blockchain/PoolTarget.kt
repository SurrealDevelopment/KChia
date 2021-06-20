package chia.types.blockchain

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PoolTarget(
    @SerialName("puzzle_hash")
    @Contextual
    val puzzleHash: Bytes32,
    @SerialName("max_height")
    val maxHeight: UInt // 0 is unlimited
)
