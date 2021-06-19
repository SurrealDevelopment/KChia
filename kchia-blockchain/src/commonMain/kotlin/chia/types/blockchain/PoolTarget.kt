package chia.types.blockchain

import chia.types.serializers.UByteArraySerializer
import kotlinx.serialization.Serializable

@Serializable
data class PoolTarget(
    @Serializable(with = UByteArraySerializer::class)
    val puzzleHash: UByteArray,
    val maxHeight: UInt // 0 is unlimited
)
