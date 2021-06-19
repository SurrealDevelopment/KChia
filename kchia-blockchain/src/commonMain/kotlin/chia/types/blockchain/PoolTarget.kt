@file:UseSerializers(BigIntegerAsStringSerializer::class, UByteArraySerializer::class, G1ElementSerializer::class)

package chia.types.blockchain

import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.G1ElementSerializer
import chia.types.serializers.UByteArraySerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class PoolTarget(
    @SerialName("puzzle_hash")
    val puzzleHash: UByteArray,
    @SerialName("max_height")
    val maxHeight: UInt // 0 is unlimited
)
