package chia.types

import kotlinx.serialization.Serializable

data class ConditionWithArgs(
    val opCode: ConditionOpCodes,
    val vars: List<UByteArray>
)
