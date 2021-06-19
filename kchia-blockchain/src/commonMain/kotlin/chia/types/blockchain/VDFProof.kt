@file:UseSerializers(BigIntegerAsStringSerializer::class, UByteArraySerializer::class, G1ElementSerializer::class)

package chia.types.blockchain

import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.G1ElementSerializer
import chia.types.serializers.UByteArraySerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class VDFInfo(
    @SerialName("challenge")
    val challenge: UByteArray,
    @SerialName("number_of_iterations")
    val numberOfIterations: ULong,
    @SerialName("output")
    val output: ClassgroupElement)

@Serializable
data class VDFProof(
    @SerialName("witness_type")
    val witnessType: UByte,
    @SerialName("witness")
    val witness: UByteArray,
    @SerialName("normalized_to_identity")
    val noramizedToidentity: Boolean
)