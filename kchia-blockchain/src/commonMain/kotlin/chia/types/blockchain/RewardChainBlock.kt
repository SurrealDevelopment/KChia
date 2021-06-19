@file:UseSerializers(BigIntegerAsStringSerializer::class, UByteArraySerializer::class, G1ElementSerializer::class)
package chia.types.blockchain

import bls.G2Element
import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.G1ElementSerializer
import chia.types.serializers.G2ElementSerializer
import chia.types.serializers.UByteArraySerializer
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class RewardChainBlockUnfinished(
    @SerialName("total_iters")
    val totalIters: BigInteger, // 128 bit limit
    @SerialName("signage_point_index")
    val signagePointIndex: UByte,
    @SerialName("pos_ss_cc_challenge_hash")
    val posSsCcChallengeHash: UByteArray,
    @SerialName("proof_of_space")
    val proofOfSpace: ProofOfSpace,
    @SerialName("challenge_chain_sp_vdf")
    val challengeChainSpVdf: VDFInfo?,
    @SerialName("challenge_chain_sp_signature")
    val challengeChainSpSignature: G2Element,
    @SerialName("reward_chain_sp_vdf")
    val rewardChainSpVdf: VDFInfo?,
    @SerialName("reward_chain_sp_signature")
    val rewardChainSpSignature: G2Element
)

@Serializable
data class RewardChainBlock(
    @SerialName("weight")
    val weight: BigInteger, //128 bit limit
    @SerialName("height")
    val height: UInt,
    @SerialName("total_iters")
    val totalIters: BigInteger,
    @SerialName("signage_point_index")
    val signagePointIndex: UByte,
    @SerialName("pos_ss_cc_challenge_hash")
    val posSsCcChallengeHash: UByteArray,
    @SerialName("proof_of_space")
    val proofOfSpace: ProofOfSpace,
    @SerialName("challenge_chain_sp_vdf")
    val challengeChainSpVdf: VDFInfo?,
    @SerialName("challenge_chain_sp_signature")
    val challengeChainSpSignature: G2Element,
    @SerialName("challenge_chain_ip_vdf")
    val challengeChainIpVdf: VDFInfo,
    @SerialName("reward_chain_sp_vdf")
    val rewardChainSpVdf: VDFInfo?,
    @SerialName("reward_chain_sp_signature")
    val rewardChainSpSignature: G2Element,
    @SerialName("reward_chain_ip_vdf")
    val rewardChainIpVdf: VDFInfo,
    @SerialName("infused_challenge_chain_ip_vdf")
    val infusedChallengeChainIpCdf: VDFInfo?, // iff deficit < 16
    @SerialName("is_transaction_block")
    val isTransactionBlock: Boolean
)
