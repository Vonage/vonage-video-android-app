package com.vonage.android.meetingroom.internal.onthego

/**
 * Fires [onSustainedMotion] once when moving samples have spanned at least
 * [thresholdMs] with no non-moving sample in between.
 *
 * A stationary, unknown, or low-confidence sample resets the run. After firing,
 * further samples are ignored until [reset]. Timestamps come from the samples,
 * so tests can compress time.
 */
internal class OnTheGoMotionDetector(
    private val thresholdMs: Long = DEFAULT_SUSTAINED_MOTION_MS,
) {
    var onSustainedMotion: (() -> Unit)? = null

    var hasFired: Boolean = false
        private set

    private var runStartMs: Long? = null

    fun ingest(sample: MotionSample) {
        if (hasFired) return
        if (!sample.isMoving) {
            runStartMs = null
            return
        }
        val start = runStartMs ?: sample.timestampMs
        runStartMs = start
        if (sample.timestampMs - start >= thresholdMs) {
            hasFired = true
            runStartMs = null
            onSustainedMotion?.invoke()
        }
    }

    fun reset() {
        hasFired = false
        runStartMs = null
    }

    companion object {
        const val DEFAULT_SUSTAINED_MOTION_MS = 20_000L
    }
}
