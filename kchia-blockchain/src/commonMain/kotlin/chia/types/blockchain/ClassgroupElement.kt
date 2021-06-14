package chia.types.blockchain

data class ClassgroupElement private constructor(
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
