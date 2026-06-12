package com.vonage.android.compose

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions

/**
 * Shared Roborazzi options for all screenshot tests.
 *
 * [RoborazziOptions.CompareOptions.changeThreshold] is set to 1 % to tolerate
 * the minor sub-pixel and anti-aliasing differences that arise when goldens
 * recorded on macOS are verified on the Linux CI runner. Robolectric NATIVE mode
 * uses JNI-compiled Skia, which can produce 1–3 pixel differences across host OSes.
 * A 1 % threshold absorbs those deltas while still catching real structural regressions
 * (wrong colour, missing element) which involve far more than 1 % of pixels.
 */
@OptIn(ExperimentalRoborazziApi::class)
object SnapshotTestDefaults {
    val OPTIONS = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0.01f),
    )
}
