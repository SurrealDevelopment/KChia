package chia.types

import chia.types.blockchain.Coin
import chia.types.blockchain.Program
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinSolution(
    @SerialName("coin")
    val coin: Coin,
    @SerialName("puzzle_reveal")
    val puzzleReveal: Program,
    @SerialName("solution")
    val solution: Program
)