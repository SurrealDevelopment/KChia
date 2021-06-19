package chia.types.blockchain

import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.UByteArraySerializer
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Serializable
import util.crypto.Sha256
import util.extensions.toTrimmed2sCompUbyteARray

@Serializable
data class Coin(
    @Serializable(with = UByteArraySerializer::class)
    val parentCoinInfo: UByteArray,
    @Serializable(with = UByteArraySerializer::class)
    val puzzleHash: UByteArray,
    @Serializable(with = BigIntegerAsStringSerializer::class)
    val amount: BigInteger
) {
    init {
        if (amount < 0) throw IllegalArgumentException()
    }

    val shaHash: UByteArray get() = Sha256().digest(
        parentCoinInfo + puzzleHash + amount.toTrimmed2sCompUbyteARray()
    )
}