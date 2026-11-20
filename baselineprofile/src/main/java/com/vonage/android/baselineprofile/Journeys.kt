package com.vonage.android.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

// Resource ids come from the Compose test tags (testTagsAsResourceId is enabled in MainActivity).
// Keep in sync with LandingScreenTestTags and WaitingRoomTestTags in :app.
private const val LANDING_SCREEN = "landing-screen"
private const val CREATE_ROOM_BUTTON = "landing-screen-create-room-button"
private const val WAITING_ROOM_SCREEN = "waiting-room-screen"

private const val UI_TIMEOUT_MS = 10_000L

private val RUNTIME_PERMISSIONS = listOf(
    "android.permission.CAMERA",
    "android.permission.RECORD_AUDIO",
    "android.permission.BLUETOOTH_CONNECT",
    "android.permission.POST_NOTIFICATIONS",
)

val targetAppId: String
    get() = requireNotNull(InstrumentationRegistry.getArguments().getString("targetAppId")) {
        "targetAppId instrumentation argument is missing"
    }

/** Grants runtime permissions up front so no system dialog interrupts the journey. */
fun MacrobenchmarkScope.grantRuntimePermissions() {
    RUNTIME_PERMISSIONS.forEach { device.executeShellCommand("pm grant $packageName $it") }
}

fun MacrobenchmarkScope.waitForLanding() {
    check(device.wait(Until.hasObject(By.res(LANDING_SCREEN)), UI_TIMEOUT_MS)) {
        "Landing screen not shown"
    }
}

/** Landing -> "Create a new room" -> waiting room. Works offline: no backend call is made. */
fun MacrobenchmarkScope.openWaitingRoom() {
    device.findObject(By.res(CREATE_ROOM_BUTTON)).click()
    check(device.wait(Until.hasObject(By.res(WAITING_ROOM_SCREEN)), UI_TIMEOUT_MS)) {
        "Waiting room not shown"
    }
    device.waitForIdle()
}
