@file:Suppress("EXPERIMENTAL_API_USAGE")

package util.crypto

/**
 * Returns the SHA256 digest of this byte array.
 */
fun ByteArray.sha256(): ByteArray = Sha256().digest(this)
fun UByteArray.sha256(): UByteArray = Sha256().digest(this)


/**
 * Returns the SHA256 digest of this string.
 */
fun String.sha256(): ByteArray = this.encodeToByteArray().sha256()