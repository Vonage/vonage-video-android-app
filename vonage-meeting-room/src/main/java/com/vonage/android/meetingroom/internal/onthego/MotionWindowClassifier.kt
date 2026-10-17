package com.vonage.android.meetingroom.internal.onthego

import kotlin.math.sqrt

/**
 * Turns a stream of acceleration magnitudes into one [MotionSample] per window.
 *
 * A window counts as moving when enough of its samples exceed a walking-sized
 * linear acceleration. That stands in for CoreMotion activity recognition
 * without a Play Services dependency. Brief dips inside the window do not reset
 * the run; a quiet window does.
 */
internal class MotionWindowClassifier(
    private val windowMs: Long = WINDOW_MS,
    private val movingMagnitude: Float = MOVING_MAGNITUDE,
    private val movingFraction: Float = MOVING_FRACTION,
) {
    private val samples = ArrayDeque<MagnitudeSample>()
    private var lastEmitMs: Long? = null

    fun onMagnitude(timeMs: Long, magnitude: Float): MotionSample? {
        samples.addLast(MagnitudeSample(timeMs, magnitude))
        trim(timeMs)
        if (!windowReady(timeMs)) return null
        lastEmitMs = timeMs
        return classifiedSample(timeMs)
    }

    private fun trim(timeMs: Long) {
        val cutoff = timeMs - windowMs
        while (samples.firstOrNull()?.let { it.timeMs < cutoff } == true) {
            samples.removeFirst()
        }
    }

    private fun windowReady(timeMs: Long): Boolean {
        val first = samples.firstOrNull()?.timeMs ?: return false
        val last = lastEmitMs
        val cooledDown = last == null || timeMs - last >= windowMs
        return cooledDown && timeMs - first >= windowMs
    }

    private fun classifiedSample(timeMs: Long): MotionSample {
        val moving = samples.count { it.magnitude >= movingMagnitude }
        val fraction = moving.toFloat() / samples.size.toFloat()
        val movingNow = fraction >= movingFraction
        val kind = if (movingNow) MotionSample.Kind.WALKING else MotionSample.Kind.STATIONARY
        val confidence = if (movingNow) MotionSample.Confidence.MEDIUM else MotionSample.Confidence.HIGH
        return MotionSample(kind = kind, confidence = confidence, timestampMs = timeMs)
    }

    private data class MagnitudeSample(val timeMs: Long, val magnitude: Float)

    companion object {
        const val WINDOW_MS = 1_000L
        const val MOVING_MAGNITUDE = 0.8f
        const val MOVING_FRACTION = 0.3f
    }
}

/**
 * Removes gravity from an accelerometer reading with a low-pass filter.
 * Linear-acceleration sensors already exclude gravity, so they pass through.
 */
internal class LinearAccelerationExtractor {
    private var gravityX = 0f
    private var gravityY = 0f
    private var gravityZ = 0f
    private var initialized = false

    fun linearMagnitude(x: Float, y: Float, z: Float, includesGravity: Boolean): Float {
        val linearX: Float
        val linearY: Float
        val linearZ: Float
        if (!includesGravity) {
            linearX = x
            linearY = y
            linearZ = z
        } else if (!initialized) {
            gravityX = x
            gravityY = y
            gravityZ = z
            initialized = true
            return 0f
        } else {
            gravityX = LOW_PASS * gravityX + HIGH_PASS * x
            gravityY = LOW_PASS * gravityY + HIGH_PASS * y
            gravityZ = LOW_PASS * gravityZ + HIGH_PASS * z
            linearX = x - gravityX
            linearY = y - gravityY
            linearZ = z - gravityZ
        }
        return sqrt(linearX * linearX + linearY * linearY + linearZ * linearZ)
    }

    companion object {
        const val LOW_PASS = 0.8f
        const val HIGH_PASS = 0.2f
    }
}
