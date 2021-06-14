package chia.types.blockchain

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Serializable
import util.crypto.Sha256
import util.extensions.toTrimmed2sCompUbyteARray

data class Coin(
    val parentCoinInfo: UByteArray,
    val puzzleHash: UByteArray,
    val amount: BigInteger
) {
    init {
        if (amount < 0) throw IllegalArgumentException()
    }

    val shaHash: UByteArray get() = Sha256().digest(
        parentCoinInfo + puzzleHash + amount.toTrimmed2sCompUbyteARray()
    )
}