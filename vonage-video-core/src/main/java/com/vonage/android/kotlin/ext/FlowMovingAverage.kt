package com.vonage.android.kotlin.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan

/**
 * Computes a simple moving average over a flow of float values.
 *
 * Uses a circular buffer to maintain a sliding window of the most recent values
 * and computes their average. This smooths out noisy data like audio levels.
 *
 * @param windowSize Number of recent values to include in the average
 * @return Flow emitting the moving average of the input values
 */
fun Flow<Float>.movingAverage(windowSize: Int): Flow<Float> =
    scan(MovingAverageState(windowSize)) { state, value -> state.add(value) }
        .map { it.average }

/**
 * Computes an exponential moving average over a flow of float values.
 *
 * Gives more weight to recent values while still considering historical data.
 * The smoothing factor determines the weight of new vs. old values.
 *
 * Formula: EMA = factor * currentValue + (1 - factor) * previousEMA
 *
 * @param factor Smoothing factor (0.0 to 1.0). Higher values give more weight to recent data.
 *               Default 0.3 provides moderate smoothing.
 * @return Flow emitting the exponential moving average of the input values
 */
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
 *
 * Uses a circular buffer for efficient O(1) updates without storing all historical values.
 * Maintains a running sum to compute the average without iteration.
 *
 * @property capacity Maximum number of values to include in the average (window size)
 */
private class MovingAverageState(private val capacity: Int) {
    private val buffer = FloatArray(capacity)
    private var size = 0
    private var index = 0
    private var sum = 0f

    /**
     * Computes the current average.
     *
     * @return Average of all values in the buffer, or 0f if empty
     */
    val average: Float
        get() = if (size > 0) sum / size else 0f

    /**
     * Adds a new value to the moving average.
     *
     * If the buffer is not full, appends the value. If full, replaces the oldest value
     * using circular buffer logic.
     *
     * @param value The new value to add
     * @return This state instance for chaining
     */
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
