package chia.types.blockchain

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClassgroupElement private constructor(
    @SerialName("data")
    @Contextual val data: Bytes100
) {
    val defaultElement get() = fromUByteArray(ubyteArrayOf(0x08u))

    companion object {
        fun fromUByteArray(array: UByteArray): ClassgroupElement {
            val clean =
                if (array.size < 100) UByteArray(100 - array.size) {0u} + array
                else array.take(100)
            return ClassgroupElement(clean.toUByteArray().asBytes100())
        }
    }
}
