package com.vonage.android.meetingroom.internal.onthego

/**
 * One motion reading, independent of the Android sensor APIs.
 *
 * [isMoving] is true only for walking, running, cycling, or automotive samples
 * whose confidence is above low. A low-confidence reading never counts.
 */
internal data class MotionSample(
    val kind: Kind,
    val confidence: Confidence,
    val timestampMs: Long,
) {
    val isMoving: Boolean
        get() = confidence != Confidence.LOW && kind in MOVING_KINDS

    enum class Kind {
        STATIONARY,
        WALKING,
        RUNNING,
        CYCLING,
        AUTOMOTIVE,
        UNKNOWN,
    }

    enum class Confidence {
        LOW,
        MEDIUM,
        HIGH,
    }

    private companion object {
        val MOVING_KINDS = setOf(Kind.WALKING, Kind.RUNNING, Kind.CYCLING, Kind.AUTOMOTIVE)
    }
}
