package chia.types

import chia.types.blockchain.Coin
import kotlinx.serialization.Serializable

data class CoinRecord(
    val coin: Coin,
    val confirmedBlockIndex: UInt,
    val spentBlockIndex: UInt,
    val spent: Boolean,
    val coinbase: Boolean,
    val timestamp: ULong
)