@file:UseSerializers(BigIntegerAsStringSerializer::class, UByteArraySerializer::class, G1ElementSerializer::class)

package chia.types.blockchain

import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.G1ElementSerializer
import chia.types.serializers.UByteArraySerializer
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import util.crypto.Sha256
import util.extensions.toTrimmed2sCompUbyteARray

@Serializable
data class Coin(
    @SerialName("parent_coin_info")
    val parentCoinInfo: UByteArray,
    @SerialName("puzzle_hash")
    val puzzleHash: UByteArray,
    @SerialName("amount")
    val amount: BigInteger
) {
    init {
        if (amount < 0) throw IllegalArgumentException()
    }

    val shaHash: UByteArray get() = Sha256().digest(
        parentCoinInfo + puzzleHash + amount.toTrimmed2sCompUbyteARray()
    )
}