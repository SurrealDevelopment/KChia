package util.hexstring

/**
 * Converts [this] [ByteArray] into its hexadecimal string representation prepending to it the given [prefix].
 *
 * Note that by default the 0x prefix is prepended to the result of the conversion.
 * If you want to have the representation without the 0x prefix, use the [toNoPrefixHexString] method or
 * pass to this method an empty [prefix].
 */
fun ByteArray.toHexStringWithPrefix(prefix: String = "0x"): String = encode(this, prefix)

@ExperimentalUnsignedTypes
fun UByteArray.toHexStringWithPrefix(prefix: String = "0x"): String = encode(this, prefix)


/**
 * Converts [this] [ByteArray] into its hexadecimal representation without prepending any prefix to it.
 */
fun ByteArray.toHexString(): String = toHexStringWithPrefix(prefix = "")

@ExperimentalUnsignedTypes
fun UByteArray.toHexString(): String = toHexStringWithPrefix(prefix = "")