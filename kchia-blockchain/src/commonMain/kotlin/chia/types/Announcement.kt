@file:UseSerializers(BigIntegerAsStringSerializer::class, UByteArraySerializer::class, G1ElementSerializer::class)

package chia.types

import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.G1ElementSerializer
import chia.types.serializers.UByteArraySerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import util.crypto.Sha256

@Serializable
data class Announcement(
    @SerialName("origin_info")
    val originInfo: UByteArray,
    @SerialName("message")
    val message: UByteArray
) {
    val name get() = Sha256().digest(originInfo + message)
}