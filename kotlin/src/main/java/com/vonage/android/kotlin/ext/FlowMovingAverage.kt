package com.vonage.android.kotlin.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan

fun Flow<Float>.movingAverage(windowSize: Int): Flow<Float> {
    require(windowSize > 0) { "Window size must be positive" }

    return scan(MovingAverageState(windowSize)) { state, value ->
        state.add(value)
    }.map { it.average }
}

fun Flow<Float>.exponentialMovingAverage(alpha: Float = 0.3f): Flow<Float> {
    require(alpha in 0f..1f) { "Alpha must be between 0 and 1" }

    return scan(null as Float?) { previousAverage, currentValue ->
        if (previousAverage == null) {
            currentValue
        } else {
            alpha * currentValue + (1 - alpha) * previousAverage
        }
    }.map { it ?: 0f }
}

/**
 * Internal state holder for moving average calculation.
 * Uses a circular buffer for efficiency.
 */
private class MovingAverageState(private val capacity: Int) {
    private val buffer = FloatArray(capacity)
    private var size = 0
    private var index = 0
    private var sum = 0f

    val average: Float
        get() = if (size > 0) sum / size else 0f

    val values: List<Float>
        get() = if (size < capacity) {
            buffer.take(size)
        } else {
            buffer.toList()
        }

    fun add(value: Float): MovingAverageState {
        if (size < capacity) {
            buffer[index] = value
            sum += value
            size++
        } else {
            // Replace oldest value
            sum = sum - buffer[index] + value
            buffer[index] = value
        }
        index = (index + 1) % capacity
        return this
    }
}
