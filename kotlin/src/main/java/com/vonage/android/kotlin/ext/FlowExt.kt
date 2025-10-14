package com.vonage.android.kotlin.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.throttleFirst(millis: Long): Flow<T> = flow {
    var lastTime = 0L
    collect { value ->
        val current = System.currentTimeMillis()
        if (current - lastTime >= millis) {
            lastTime = current
            emit(value)
        }
    }
}
