@file:UseSerializers(BigIntegerAsStringSerializer::class, UByteArraySerializer::class, G1ElementSerializer::class)


package chia.types.blockchain

import chia.types.serializers.BigIntegerAsStringSerializer
import chia.types.serializers.G1ElementSerializer
import chia.types.serializers.UByteArraySerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class ClassgroupElement private constructor(
    @SerialName("data")
    val data: UByteArray
) {
    val defaultElement get() = fromUByteArray(ubyteArrayOf(0x08u))

    companion object {
        fun fromUByteArray(array: UByteArray): ClassgroupElement {
            val clean =
                if (array.size < 100) UByteArray(100 - array.size) {0u} + array
                else array.take(100)
            return ClassgroupElement(clean.toUByteArray())
        }
    }
}
