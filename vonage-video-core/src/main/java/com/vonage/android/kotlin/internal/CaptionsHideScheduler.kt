package com.vonage.android.kotlin.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Schedules the delayed removal of caption lines after a speaker's final segment.
 *
 * One [Job] is kept per [streamId]. Scheduling the same stream again cancels the
 * previous job. Calling [cancelAll] is safe to call during session teardown.
 *
 * @param coroutineScope Scope used to launch delay jobs (typically Call's own scope).
 * @param hideDelayMs Time in ms to wait before invoking [onHide]. Defaults to
 *   [CAPTIONS_HIDE_DELAY_MS]. Override in tests via virtual-time or direct injection.
 * @param onHide Called with the [streamId] whose line should be removed from state.
 */
internal class CaptionsHideScheduler(
    private val coroutineScope: CoroutineScope,
    private val hideDelayMs: Long = CAPTIONS_HIDE_DELAY_MS,
    private val onHide: (streamId: String) -> Unit,
) {
    private val jobs = mutableMapOf<String, Job>()

    /** Schedules [onHide] for [streamId] after [hideDelayMs]. Cancels any prior job. */
    fun schedule(streamId: String) {
        jobs[streamId]?.cancel()
        jobs[streamId] = coroutineScope.launch {
            delay(hideDelayMs)
            onHide(streamId)
            jobs.remove(streamId)
        }
    }

    /**
     * Cancels any pending hide job for [streamId].
     * Call this when a new (non-final) caption arrives for the same stream.
     */
    fun cancel(streamId: String) {
        jobs[streamId]?.cancel()
        jobs.remove(streamId)
    }

    /** Cancels all pending jobs. Call this when captions are disabled or session ends. */
    fun cancelAll() {
        jobs.values.forEach { it.cancel() }
        jobs.clear()
    }

    private companion object {
        const val CAPTIONS_HIDE_DELAY_MS = 2_000L
    }
}
