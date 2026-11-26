package com.vonage.android

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.metrics.performance.JankStats
import com.vonage.logger.vonageLogger
import java.util.WeakHashMap

/**
 * Logs janky frames for every activity. Debug builds only: JankStats is a debugImplementation dependency.
 * Filter logcat by [TAG] to see them.
 */
internal class JankStatsTracker : Application.ActivityLifecycleCallbacks {

    private val trackers = WeakHashMap<Activity, JankStats>()

    // Created on first resume rather than in onCreate, so the window's content is already set.
    override fun onActivityResumed(activity: Activity) {
        trackers.getOrPut(activity) { createTracker(activity) }.isTrackingEnabled = true
    }

    override fun onActivityPaused(activity: Activity) {
        trackers[activity]?.isTrackingEnabled = false
    }

    override fun onActivityDestroyed(activity: Activity) {
        trackers.remove(activity)?.isTrackingEnabled = false
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    private fun createTracker(activity: Activity): JankStats {
        val screen = activity.javaClass.simpleName
        return JankStats.createAndTrack(activity.window) { frame ->
            if (frame.isJank) {
                val durationMs = frame.frameDurationUiNanos / NANOS_PER_MILLI
                vonageLogger.w(TAG, "Janky frame in $screen: ${durationMs}ms, states=${frame.states}")
            }
        }
    }

    private companion object {
        const val TAG = "JankStats"
        const val NANOS_PER_MILLI = 1_000_000L
    }
}
