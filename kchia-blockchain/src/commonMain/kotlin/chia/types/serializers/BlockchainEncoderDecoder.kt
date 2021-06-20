package chia.types.serializers

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import util.extensions.readUInt
import util.extensions.readULong
import util.extensions.toBytes


/**
 * Used for encoding objects to a well defined blockchain format
 */
object BlockchainData {
    inline fun <reified T> encodeToBlockchain(value: T) = BlockchainEncoder.encodeToBlockchainFormat(serializer(), value)
    inline fun <reified T> decodeFromBlockchain(value: UByteArray): T = BlockchainDecoder.decodeFromUByteArray(value, serializer())
}

// big endian data
data class Data(var arr: List<UByte>) {
    fun readInt(): Int {
        val x = arr.take(4).toUByteArray().readUInt().toInt()
        arr = arr.drop(4)
        return x
    }
    fun readShort(): Short {
        val x = arr.take(2).toUByteArray().readUInt().toShort()
        arr = arr.drop(2)
        return x
    }
    fun readLong(): Long {
        val x = arr.take(8).toUByteArray().readULong().toLong()
        arr = arr.drop(8)
        return x
    }
    fun readByte(): Byte {
        val x = arr[0].toByte()
        arr = arr.drop(1)
        return x
    }
    fun readBytes(n: Int): List<UByte> {
        val x = arr.take(n)
        arr = arr.drop(n)
        return x
    }

}


@OptIn(ExperimentalSerializationApi::class, ExperimentalUnsignedTypes::class)
class BlockchainEncoder(val descriptor: SerialDescriptor): AbstractEncoder() {

    private var arr = UByteArray(0)

    override val serializersModule: SerializersModule
        get() = Modules.bytes

    override fun encodeByte(value: Byte) {
        arr += value.toUByte()
    }
    private fun encodeUByteArray(byteArray: UByteArray) {
        arr += (byteArray.size).toBytes(4) + byteArray
    }
    override fun encodeShort(value: Short) {
        arr += value.toUInt().toBytes(2)
    }
    override fun encodeInt(value: Int) {
        arr += value.toUInt().toBytes(4)
    }
    override fun encodeLong(value: Long) {
        arr += value.toULong().toBytes(8)
    }
    override fun encodeNotNullMark() {
        encodeBoolean(true)
    }

    override fun encodeString(value: String) {
        // size then utf8
        val bytes = value.encodeToByteArray().toUByteArray()
        throw Exception()
        encodeUByteArray(bytes)
    }

    override fun encodeBoolean(value: Boolean) {
        arr = arr + if (value) (1u).toUByte() else (0u).toUByte()
    }
    override fun encodeNull() {
        encodeBoolean(false)
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        arr += collectionSize.toUInt().toBytes(4)
        return this
    }

    companion object {
        fun <T> encodeToBlockchainFormat(serializer: SerializationStrategy<T>, value: T): UByteArray {
            val encoder = BlockchainEncoder(serializer.descriptor)
            encoder.encodeSerializableValue(serializer, value)
            return encoder.arr
        }
    }
}


@OptIn(ExperimentalSerializationApi::class, ExperimentalUnsignedTypes::class)
class BlockchainDecoder(val data: Data, val descriptor: SerialDescriptor): AbstractDecoder() {

    var position = 0
    override val serializersModule: SerializersModule
        get() = Modules.bytes

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (position == descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
        return position++
    }

    override fun decodeSequentially(): Boolean  = true

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return BlockchainDecoder(data, descriptor)
    }

    override fun decodeBoolean(): Boolean = data.readByte() > 0
    override fun decodeByte(): Byte = data.readByte()
    override fun decodeShort(): Short = data.readShort()
    override fun decodeInt(): Int = data.readInt()
    override fun decodeLong(): Long = data.readLong()
    override fun decodeString(): String {
        val size = data.readInt()
        val bytes = data.readBytes(size).toUByteArray().toByteArray()
        return bytes.decodeToString()
    }


    override fun decodeNotNullMark(): Boolean {
        return decodeBoolean()
    }


    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        // copllection size always 4 bytes
        return decodeInt()
    }


    companion object {
        fun <T> decodeFromUByteArray(arr: UByteArray, deserializer: DeserializationStrategy<T>): T {
            val decoder = BlockchainDecoder(Data(arr.toList()), deserializer.descriptor)
            return decoder.decodeSerializableValue(deserializer)
        }
    }


}