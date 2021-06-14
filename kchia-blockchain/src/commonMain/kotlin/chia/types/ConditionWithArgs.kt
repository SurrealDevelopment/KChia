package chia.types

data class ConditionWithArgs(
    val opCode: ConditionOpCodes,
    val vars: List<UByteArray>
)
