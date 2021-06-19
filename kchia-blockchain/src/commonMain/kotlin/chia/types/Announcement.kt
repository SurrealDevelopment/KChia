package chia.types

import chia.types.serializers.UByteArraySerializer
import kotlinx.serialization.Serializable
import util.crypto.Sha256

@Serializable
data class Announcement(
    @Serializable(with = UByteArraySerializer::class)
    val originInfo: UByteArray,
    @Serializable(with = UByteArraySerializer::class)
    val message: UByteArray
) {
    val name get() = Sha256().digest(originInfo + message)
}