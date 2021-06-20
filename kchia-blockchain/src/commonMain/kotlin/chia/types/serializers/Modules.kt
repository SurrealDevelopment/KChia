package chia.types.serializers

import chia.types.blockchain.*
import chia.types.blockchain.makeStringSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

/**
 * Use the appropiate module for your serialization.
 * Bytes is suitable for blockchain serialization
 * Strings is suitable for web, json, etc..
 */
object Modules {

    val strings = SerializersModule {
        contextual(BigIntegerAsStringSerializer())
        contextual(G1ElementAsStringSerializer())
        contextual(G2ElementAsStringSerializer())
        contextual(ProgramAsStringSerializer())
        contextual(UByteArrayAsStringSerializer())
        contextual(makeStringSerializer<Bytes480>())
        contextual(makeStringSerializer<Bytes100>())
        contextual(makeStringSerializer<Bytes96>())
        contextual(makeStringSerializer<Bytes48>())
        contextual(makeStringSerializer<Bytes32>())
        contextual(makeStringSerializer<Bytes8>())
        contextual(makeStringSerializer<Bytes4>())


    }

    val bytes = SerializersModule {
        contextual(BigIntegerAs128Serializer())
        contextual(G1ElementAsBytesSerializer())
        contextual(G2ElementAsBytesSerializer())
        contextual(ProgramAsBytesSerializer())
        contextual(UByteArraySizedSerrializer())
        contextual(makeBytesSerializer<Bytes480>())
        contextual(makeBytesSerializer<Bytes100>())
        contextual(makeBytesSerializer<Bytes96>())
        contextual(makeBytesSerializer<Bytes48>())
        contextual(makeBytesSerializer<Bytes32>())
        contextual(makeBytesSerializer<Bytes8>())
        contextual(makeBytesSerializer<Bytes4>())
    }
}