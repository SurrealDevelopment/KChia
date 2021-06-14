package chia.types.blockchain

import bls.G1Element

data class ProofOfSpace(
    val challenge: UByteArray,
    val poolPublicKey: UByteArray,
    val poolContractPuzzleHash: UByteArray?,
    val plotPublicKey: G1Element,
    val size: Int,
    val proof: UByteArray
)