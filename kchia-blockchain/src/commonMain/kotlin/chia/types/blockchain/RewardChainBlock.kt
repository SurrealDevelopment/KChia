package chia.types.blockchain

import bls.G2Element
import com.ionspin.kotlin.bignum.integer.BigInteger

data class RewardChainBlockUnfinished(
    val totalIters: BigInteger, // 128 bit limit
    val signagePointIndex: UByte,
    val posSsCcChallengeHash: UByteArray,
    val proofOfSpace: ProofOfSpace,
    val challengeChainSpVdf: VDFInfo?,
    val challengeChainSpSignature: G2Element,
    val rewardChainSpVdf: VDFInfo?,
    val rewardChainSpSignature: G2Element
)

data class RewardChainBlock(
    val weight: BigInteger, //128 bit limit
    val height: UInt,
    val totalIters: BigInteger,
    val signagePointIndex: UByte,
    val posSsCcChallengeHash: UByteArray,
    val proofOfSpace: ProofOfSpace,
    val challengeChainSpVdf: VDFInfo?,
    val challengeChainSpSignature: G2Element,
    val challengeChainIpVdf: VDFInfo,
    val rewardChainSpVdf: VDFInfo?,
    val rewardChainSpSignature: G2Element,
    val rewardChainIpVdf: VDFInfo,
    val infusedChallengeChainIpCdf: VDFInfo?, // iff deficit < 16
    val isTransactionBlock: Boolean
)
