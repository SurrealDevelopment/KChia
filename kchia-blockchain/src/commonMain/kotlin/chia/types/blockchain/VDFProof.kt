package chia.types.blockchain

data class VDFInfo(
    val challenge: UByteArray,
    val numberOfIterations: ULong,
    val output: ClassgroupElement)

data class VDFProof(
    val witnessType: UByte,
    val witness: UByteArray,
    val noramizedToidentity: Boolean
)