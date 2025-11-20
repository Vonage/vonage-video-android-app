package com.vonage.android.kotlin.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan

fun Flow<Float>.movingAverage(windowSize: Int): Flow<Float> =
    scan(MovingAverageState(windowSize)) { state, value -> state.add(value) }
        .map { it.average }

fun Flow<Float>.exponentialMovingAverage(factor: Float = 0.3f): Flow<Float> =
    scan(null as Float?) { previousAverage, currentAverage ->
        if (previousAverage == null) {
            currentAverage
        } else {
            factor * currentAverage + (1 - factor) * previousAverage
        }
    }.map { it ?: 0f }

/**
 * Internal state holder for moving average calculation.
 * Uses a circular buffer
 */
private class MovingAverageState(private val capacity: Int) {
    private val buffer = FloatArray(capacity)
    private var size = 0
    private var index = 0
    private var sum = 0f

    val average: Float
        get() = if (size > 0) sum / size else 0f

    fun add(value: Float): MovingAverageState {
        if (size < capacity) {
            buffer[index] = value
            sum += value
            size++
        } else {
            sum = sum - buffer[index] + value
            buffer[index] = value
        }
        index = (index + 1) % capacity
        return this
    }
}
