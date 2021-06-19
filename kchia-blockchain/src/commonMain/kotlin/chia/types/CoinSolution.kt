package chia.types

import chia.types.blockchain.Coin
import chia.types.blockchain.Program
import kotlinx.serialization.Serializable

@Serializable
data class CoinSolution(
    val coin: Coin,
    val puzzleReveal: Program,
    val solution: Program
)