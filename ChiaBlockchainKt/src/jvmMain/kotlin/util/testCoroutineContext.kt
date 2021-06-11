package util

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

actual val testCoroutineContext: CoroutineContext =
    Executors.newCachedThreadPool().asCoroutineDispatcher()


actual fun <R> runBlockingTest(block: suspend () -> R): R {
    return runBlocking(testCoroutineContext) { block() }
}
