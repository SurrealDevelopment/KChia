package chia

import chia.types.blockchain.Coin
import chia.types.blockchain.asBytes32
import chia.types.serializers.BlockchainData
import util.hexstring.asHexString
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BlockchainSerializationTests {
    val someG2 = "a1a2d4c138af9feffcda7bee8b2eb034f962a4a49afc24a7fe77775a80896ba5c55bb3e4bd8ad8ae46fc2dcc6986ea7b"
        .asHexString().toUByteArray()
    val some32 = "a1a2d4c138af9feffcda7bee8b2eb034f962a4a49afc24a7fe77775a80896ba5"
        .asHexString().toUByteArray().asBytes32()


    @Test
    fun testSerailization() {
        val coin = Coin(
            some32,
            some32,
            111111111UL
        )
        val bytes = BlockchainData.encodeToBlockchain(coin)
        val outCoin = BlockchainData.decodeFromBlockchain<Coin>(bytes)
        assertContentEquals(coin.parentCoinInfo, outCoin.parentCoinInfo)
        assertContentEquals(coin.puzzleHash, outCoin.puzzleHash)
        assertEquals(coin.amount, outCoin.amount)
        assertContentEquals(coin.shaHash, outCoin.shaHash)
    }
}