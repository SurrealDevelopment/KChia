package chia.types.blockchain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import util.hexstring.asHexString
import util.hexstring.toHexString

/**
 * Fixed sizes allow us to serialize in place (no length descriptor)
 * And provides some extra tests for blockchain use
 *
 * Warning. Editing this file may be buggy due to the reified generics.
 */

abstract class SizedBytes(val bytes: UByteArray)
    : Collection<UByte> by bytes
{
    operator fun plus(other: UByteArray): UByteArray = this.bytes + other
    operator fun plus(other: SizedBytes): UByteArray = this.bytes + other.bytes

    override fun equals(other: Any?): Boolean {
        if (other is SizedBytes)
            return this.bytes.contentEquals(other.bytes)
        else if (other is UByteArray)
            return this.bytes.contentEquals(other)
        else return false
    }
}

class Bytes480(bytes: UByteArray): SizedBytes(bytes) {
    init {
        if (bytes.size != 480) throw IllegalArgumentException()
    }
}

class Bytes100(bytes: UByteArray): SizedBytes(bytes) {
    init {
        if (bytes.size != 100) throw IllegalArgumentException()
    }
}

class Bytes96(bytes: UByteArray): SizedBytes(bytes) {
    init {
        if (bytes.size != 96) throw IllegalArgumentException()
    }
}

class Bytes48(bytes: UByteArray): SizedBytes(bytes) {
    init {
        if (bytes.size != 48) throw IllegalArgumentException()
    }
}


class Bytes32(bytes: UByteArray): SizedBytes(bytes) {
    init {
        if (bytes.size != 32) throw IllegalArgumentException()
    }
}

class Bytes8(bytes: UByteArray): SizedBytes(bytes) {
    init {
        if (bytes.size != 8) throw IllegalArgumentException()
    }
}

class Bytes4(bytes: UByteArray): SizedBytes(bytes) {
    init {
        if (bytes.size != 4) throw IllegalArgumentException()
    }
}

internal inline fun <reified T> UByteArray.refiedCast(): T {
    return when (T::class) {
        Bytes480::class -> Bytes480(this) as T
        Bytes100::class -> Bytes100(this) as T
        Bytes96::class -> Bytes96(this) as T
        Bytes48::class -> Bytes48(this) as T
        Bytes32::class -> Bytes32(this) as T
        Bytes8::class -> Bytes8(this) as T
        Bytes4::class -> Bytes8(this) as T

        else -> throw IllegalArgumentException("Class not defined")
    }
}

internal inline fun <reified T> sizeFor(): Int {
    return when (T::class) {
        Bytes480::class -> 480
        Bytes100::class -> 100
        Bytes96::class -> 96
        Bytes48::class -> 48
        Bytes32::class -> 32
        Bytes8::class -> 8
        Bytes4::class -> 4
        else -> throw IllegalArgumentException("Class not defined")
    }
}

internal inline fun <reified T: SizedBytes> makeStringSerializer(): KSerializer<T> {
    return object : KSerializer<T> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("BytesStr${sizeFor<T>()}",PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): T {
            return decoder.decodeString().asHexString().toUByteArray().refiedCast()
        }
        override fun serialize(encoder: Encoder, value: T) {
            encoder.encodeString(value.bytes.toHexString())
        }
    }
}

internal inline fun <reified T: SizedBytes> makeBytesSerializer(): KSerializer<T> {
    return object : KSerializer<T> {
        override val descriptor: SerialDescriptor
            get() = buildClassSerialDescriptor("Bytes${sizeFor<T>()}")

        override fun deserialize(decoder: Decoder): T {
            val size = sizeFor<T>()
            var bytes = byteArrayOf()
            for (i in 0 until size) {
                bytes += decoder.decodeByte()
            }
            return bytes.toUByteArray().refiedCast()
        }
        override fun serialize(encoder: Encoder, value: T) {
            for (it in value.bytes) {
                encoder.encodeByte(it.toByte()) // just encode in place. No size
            }
        }
    }
}

fun UByteArray.asBytes480(): Bytes480 = Bytes480(this)
fun UByteArray.asBytes100(): Bytes100 = Bytes100(this)
fun UByteArray.asBytes96(): Bytes96 = Bytes96(this)
fun UByteArray.asBytes48(): Bytes48 = Bytes48(this)
fun UByteArray.asBytes32(): Bytes32 = Bytes32(this)
fun UByteArray.asBytes8(): Bytes8 = Bytes8(this)
fun UByteArray.asBytes4(): Bytes4 = Bytes4(this)
