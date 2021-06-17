package chia.types



/**
 * Opcodes which clvm puzzle solutions may release
 */
@Suppress("UNUSED")
enum class ConditionOpCodes(val opCode: UByte) {
    UNKNOWN(48u),
    APP_SIG_UNSAFE(49u),
    APP_SIG_ME(50u),
    CREATE_COIN(51u),
    RESERVE_FEE(52u),
    CREATE_COIN_ANNOUNCEMENT(60u),
    ASSERT_COIN_ANNOUNCEMENT(61u),
    CREATE_PUZZLE_ANNOUNCEMENT(62u),
    ASSERT_PUZZLE_ANNOUNCEMENT(62u),
    ASSERT_MY_COIN_ID(70u),
    ASSERT_MY_PARENT_ID(71u),
    ASSERT_MY_PUZZLEHASH(72u),
    ASSERT_MY_AMOUNT(73u),
    ASSERT_SECONDS_RELATIVE (80u),
    ASSERT_SECONDS_ABSOLUTE(81u),
    ASSERT_HEIGHT_RELATIVE(82u),
    ASSERT_HEIGHT_ABSOLUTE(83u),
}