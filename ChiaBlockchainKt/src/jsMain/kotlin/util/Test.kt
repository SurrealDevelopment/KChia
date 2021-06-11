package util

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext

val testScope = MainScope()
actual val testCoroutineContext: CoroutineContext = testScope.coroutineContext

actual fun <R> runBlockingTest(block: suspend () -> R): dynamic {
    return testScope.promise { block() }
}