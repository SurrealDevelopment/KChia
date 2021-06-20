package chia.types


import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConditionWithArgs(
    @SerialName("op_code")
    val opCode: ConditionOpCodes,
    @SerialName("vars")

    val vars: List<@Contextual UByteArray>
)
