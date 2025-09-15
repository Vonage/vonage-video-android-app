package com.vonage.android.util.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class CoroutinePollerProvider<T> @Inject constructor() {
    fun get(
        dispatcher: CoroutineDispatcher,
        fetchData: suspend () -> T,
    ): CoroutinePoller<T> =
        CoroutinePoller(dispatcher, fetchData)
}

class CoroutinePoller<T>(
    private val dispatcher: CoroutineDispatcher,
    private val fetchData: suspend () -> T,
) {
    var job: Job? = null

    fun poll(delay: Long) = callbackFlow {
        job = launch(dispatcher) {
            while (isActive) {
                val data = fetchData()
                trySend(data)
                delay(delay)
            }
        }
        awaitClose {
            cancel()
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}
