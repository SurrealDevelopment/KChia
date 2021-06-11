
package chia.puzzles

import chia.clisp_high_level.Clvmc
import chia.types.blockchain.Program
import util.file.readBytesOfResource
import util.hexstring.asHexString



private fun loadSerializedClvm(fileName: String): Program {

    val hexName = "$fileName.hex"

    return try {
        val clvm = readBytesOfResource("puzzles/$fileName").decodeToString()
        val sexp = Clvmc.compileFromText(clvm, listOf("puzzles"))
        Program(sexp)
    } catch (e: Exception) {
        // try hex
        val clvmHex = readBytesOfResource("puzzles/$hexName").decodeToString().asHexString()
        Program.fromByteArray(clvmHex.toByteArray())
    }
}
/**
 * Load program from a .hex file or compiles it if not already
 */
fun loadClvm(fileName: String): Program {
    return loadSerializedClvm(fileName)
}