package chia.types

import chia.types.blockchain.Coin
import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.G1ElementAsStringSerializer
import chia.types.serializers.UByteArrayAsStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class CoinRecord(
    @SerialName("coin")
    val coin: Coin,
    @SerialName("confirmed_block_index")
    val confirmedBlockIndex: UInt,
    @SerialName("spent_block_index")
    val spentBlockIndex: UInt,
    @SerialName("spent")
    val spent: Boolean,
    @SerialName("coinbase")
    val coinbase: Boolean,
    @SerialName("timestamp")
    val timestamp: ULong
)