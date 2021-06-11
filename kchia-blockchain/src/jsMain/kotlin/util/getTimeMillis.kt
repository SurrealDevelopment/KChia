package util

import kotlin.js.Date

actual fun getTimeMillis(): Long {
    return Date().getMilliseconds().toLong()
}