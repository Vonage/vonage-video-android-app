package com.vonage.android.kotlin.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Buffers flow emissions into chunks of a specified size and transforms them.
 *
 * Collects emissions from the upstream flow into lists of the specified size,
 * then applies the transform function to each complete chunk before emitting.
 * Similar to Kotlin's chunked() for sequences but for flows.
 *
 * @param T The type of elements in the upstream flow
 * @param R The type of elements in the transformed flow
 * @param size Number of elements to collect before transforming and emitting
 * @param transform Suspend function to apply to each chunk of elements
 * @return Flow emitting the transformed chunks
 */
fun <T, R> Flow<T>.chunked(size: Int, transform: suspend (List<T>) -> R): Flow<R> = flow {
    val cache = ArrayList<T>(size)
    collect {
        cache.add(it)
        if (cache.size == size) {
            emit(transform(cache))
            cache.clear()
        }
    }
}
