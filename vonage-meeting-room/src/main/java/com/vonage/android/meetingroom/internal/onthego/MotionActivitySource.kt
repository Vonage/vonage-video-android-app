package com.vonage.android.meetingroom.internal.onthego

/**
 * Source of [MotionSample]s.
 *
 * [start] delivers samples on the main thread. Starting does not request a permission.
 */
internal interface MotionActivitySource {
    val isAvailable: Boolean

    fun start(handler: (MotionSample) -> Unit)

    fun stop()
}
