package com.vonage.android.baselineprofile

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Keeps the Landing screen idle and counts the work Compose does while nothing changes.
 * An idle screen should not recompose or re-lay out: a non-zero count usually means an
 * animated value is read during composition instead of in the draw phase.
 *
 * Counts are stable on emulators, so this also runs on the Gradle Managed Device:
 * `./gradlew :baselineprofile:pixel6Api34BenchmarkReleaseAndroidTest`
 */
@OptIn(ExperimentalMetricApi::class)
@RunWith(AndroidJUnit4::class)
class IdleLandingBenchmark {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun idleLanding() = rule.measureRepeated(
        packageName = targetAppId,
        metrics = listOf(
            TraceSectionMetric("Compose:recompose", TraceSectionMetric.Mode.Count, label = IDLE_RECOMPOSE),
            TraceSectionMetric("AndroidOwner:measureAndLayout", TraceSectionMetric.Mode.Count, label = IDLE_LAYOUT),
            FrameTimingMetric(),
        ),
        iterations = ITERATIONS,
        setupBlock = {
            killProcess()
            grantRuntimePermissions()
            startActivityAndWait()
            waitForLanding()
            device.waitForIdle()
        },
    ) {
        Thread.sleep(IDLE_MS)
    }

    private companion object {
        const val ITERATIONS = 3
        const val IDLE_MS = 10_000L
        const val IDLE_RECOMPOSE = "idleLandingRecompose"
        const val IDLE_LAYOUT = "idleLandingMeasureAndLayout"
    }
}
