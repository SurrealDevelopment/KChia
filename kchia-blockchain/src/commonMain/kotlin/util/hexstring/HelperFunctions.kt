package util.hexstring

@ExperimentalUnsignedTypes
fun uByteArrayFrom(hexString: String): UByteArray {
    return HexString(hexString).toUByteArray()
}

fun byteArrayFrom(hexString: String): ByteArray {
    return HexString(hexString).toByteArray()
}

fun String.asHexString(): HexString {
    return HexString(this)
}