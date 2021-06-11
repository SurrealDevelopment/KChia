package util

import kotlin.coroutines.CoroutineContext

expect fun <R> runBlockingTest(block: suspend () -> R): R
expect val testCoroutineContext: CoroutineContext

