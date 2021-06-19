package chia.types.blockchain

import chia.types.serializers.G2ElementSerializer
import chia.types.serializers.UByteArraySerializer
import kotlinx.serialization.Serializable

@Serializable
data class VDFInfo(
    @Serializable(with = UByteArraySerializer::class)
    val challenge: UByteArray,
    val numberOfIterations: ULong,
    val output: ClassgroupElement)

@Serializable
data class VDFProof(
    val witnessType: UByte,
    @Serializable(with = UByteArraySerializer::class)
    val witness: UByteArray,
    val noramizedToidentity: Boolean
)