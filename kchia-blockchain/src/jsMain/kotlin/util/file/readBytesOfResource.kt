package util.file

import org.w3c.xhr.XMLHttpRequest

// TOdo switch to web pack and do async
actual fun readBytesOfResource(path: String): ByteArray {
    val x = XMLHttpRequest()
    val url = "/$path"
    x.open("GET",
        "/$path",
    false)
    x.send()
    if (x.status != (200).toShort()) {
        throw Exception("Cannot get $url, ${x.status}: ${x.statusText}")
    }
    return x.responseText.encodeToByteArray()
}

actual fun isFile(path: String): Boolean {
    return try {
        readBytesOfResource(path)
        true
    } catch (e: Exception) {
        console.log(e)
        false
    }
}