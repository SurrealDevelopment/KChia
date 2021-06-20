package chia.types.blockchain

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import util.crypto.Sha256
import util.extensions.toBytes

@Serializable
data class Coin(
    @SerialName("parent_coin_info")
    @Contextual
    val parentCoinInfo: Bytes32,
    @SerialName("puzzle_hash")
    @Contextual
    val puzzleHash: Bytes32,
    @SerialName("amount")
    val amount: ULong
) {
    init {
        if (amount < 0uL) throw IllegalArgumentException()
    }

    val shaHash: UByteArray get() = Sha256().digest(
        parentCoinInfo + puzzleHash + amount.toBytes(8)
    )
}