package util.file

import java.io.File

actual fun readBytesOfResource(path: String): ByteArray {
    val file = { }::class.java.getResource(path)
        ?: throw Exception("does not exist or ?")

    return file.readBytes()
}

actual fun isFile(path: String): Boolean {
    val file = { }::class.java.getResource(path)
        ?: return false

    val f = File(file.path)

    return f.exists()
}