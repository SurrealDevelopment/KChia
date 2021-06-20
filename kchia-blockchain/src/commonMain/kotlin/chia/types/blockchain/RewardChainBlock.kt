package chia.types.blockchain

import bls.G2Element
import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.G1ElementAsStringSerializer
import chia.types.serializers.UByteArrayAsStringSerializer
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class RewardChainBlockUnfinished(
    @SerialName("total_iters")
    @Contextual
    val totalIters: BigInteger, // 128 bit limit
    @SerialName("signage_point_index")
    val signagePointIndex: UByte,
    @SerialName("pos_ss_cc_challenge_hash")
    @Contextual
    val posSsCcChallengeHash: Bytes32,
    @SerialName("proof_of_space")
    val proofOfSpace: ProofOfSpace,
    @SerialName("challenge_chain_sp_vdf")
    val challengeChainSpVdf: VDFInfo?,
    @SerialName("challenge_chain_sp_signature")
    @Contextual
    val challengeChainSpSignature: G2Element,
    @SerialName("reward_chain_sp_vdf")
    val rewardChainSpVdf: VDFInfo?,
    @SerialName("reward_chain_sp_signature")
    @Contextual
    val rewardChainSpSignature: G2Element
)

@Serializable
data class RewardChainBlock(
    @SerialName("weight")
    @Contextual
    val weight: BigInteger, //128 bit limit
    @SerialName("height")
    val height: UInt,
    @SerialName("total_iters")
    @Contextual
    val totalIters: BigInteger,
    @SerialName("signage_point_index")
    val signagePointIndex: UByte,
    @SerialName("pos_ss_cc_challenge_hash")
    @Contextual
    val posSsCcChallengeHash: Bytes32,
    @SerialName("proof_of_space")
    val proofOfSpace: ProofOfSpace,
    @SerialName("challenge_chain_sp_vdf")
    val challengeChainSpVdf: VDFInfo?,
    @SerialName("challenge_chain_sp_signature")
    @Contextual
    val challengeChainSpSignature: G2Element,
    @SerialName("challenge_chain_ip_vdf")
    val challengeChainIpVdf: VDFInfo,
    @SerialName("reward_chain_sp_vdf")
    val rewardChainSpVdf: VDFInfo?,
    @SerialName("reward_chain_sp_signature")
    @Contextual
    val rewardChainSpSignature: G2Element,
    @SerialName("reward_chain_ip_vdf")
    val rewardChainIpVdf: VDFInfo,
    @SerialName("infused_challenge_chain_ip_vdf")
    val infusedChallengeChainIpCdf: VDFInfo?, // iff deficit < 16
    @SerialName("is_transaction_block")
    val isTransactionBlock: Boolean
)
