package chia.types

import util.crypto.Sha256

data class Announcement(
    val originInfo: UByteArray,
    val message: UByteArray
) {
    val name get() = Sha256().digest(originInfo + message)
}