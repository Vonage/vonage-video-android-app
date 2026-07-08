package com.vonage.android.kotlin.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Schedules the delayed removal of caption lines after a speaker's final segment.
 *
 * One [Job] is kept per [streamId]. Scheduling the same stream again cancels the
 * previous job. Calling [cancelAll] is safe to call during session teardown.
 *
 * Thread safety: [schedule], [cancel], and [cancelAll] may be called from any thread
 * (e.g. the OpenTok SDK callback thread). [ConcurrentHashMap] is used for the jobs map
 * so that concurrent reads/writes are safe. Each launched job uses [ensureActive] after
 * the delay to guard against a cancellation that arrived just as the delay completed, and
 * uses identity-based [ConcurrentHashMap.remove] to avoid evicting a newer job's entry.
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
    private val jobs = ConcurrentHashMap<String, Job>()

    /** Schedules [onHide] for [streamId] after [hideDelayMs]. Cancels any prior job. */
    fun schedule(streamId: String) {
        jobs[streamId]?.cancel()
        jobs[streamId] = coroutineScope.launch {
            delay(hideDelayMs)
            // Guard against a cancellation that arrived just as the delay completed.
            ensureActive()
            onHide(streamId)
            // Only remove this stream's entry if it still refers to this exact job.
            // Prevents a completing job from evicting a newer job that was scheduled
            // for the same stream while this job was finishing.
            jobs.remove(streamId, coroutineContext[Job])
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
