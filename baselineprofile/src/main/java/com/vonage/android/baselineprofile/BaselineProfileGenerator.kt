package com.vonage.android.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates the app's Baseline Profile. Run with:
 * `./gradlew :app:generateBaselineProfile`
 * The result is copied to `app/src/release/generated/baselineProfiles/` and must be committed.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun startup() = rule.collect(
        packageName = targetAppId,
        includeInStartupProfile = true,
    ) {
        pressHome()
        startActivityAndWait()
        waitForLanding()
    }

    @Test
    fun landingToWaitingRoom() = rule.collect(
        packageName = targetAppId,
        includeInStartupProfile = false,
    ) {
        grantRuntimePermissions()
        pressHome()
        startActivityAndWait()
        waitForLanding()
        openWaitingRoom()
    }
}
