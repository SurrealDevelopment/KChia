package chia.types

import chia.types.blockchain.Bytes32
import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.G1ElementAsStringSerializer
import chia.types.serializers.UByteArrayAsStringSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import util.crypto.Sha256

@Serializable
data class Announcement(
    @SerialName("origin_info")
    @Contextual
    val originInfo: Bytes32,
    @SerialName("message")
    @Contextual
    val message: UByteArray
) {
    val name get() = Sha256().digest(originInfo + message)
}