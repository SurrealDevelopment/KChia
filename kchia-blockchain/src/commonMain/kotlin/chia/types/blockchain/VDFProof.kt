package chia.types.blockchain

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VDFInfo(
    @SerialName("challenge")
    @Contextual
    val challenge: Bytes32,
    @SerialName("number_of_iterations")
    val numberOfIterations: ULong,
    @SerialName("output")
    val output: ClassgroupElement)

@Serializable
data class VDFProof(
    @SerialName("witness_type")
    val witnessType: UByte,
    @SerialName("witness")
    @Contextual
    val witness: UByteArray,
    @SerialName("normalized_to_identity")
    val noramizedToidentity: Boolean
)