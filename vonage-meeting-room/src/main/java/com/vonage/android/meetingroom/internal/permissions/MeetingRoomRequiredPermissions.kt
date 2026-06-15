package com.vonage.android.meetingroom.internal.permissions

import android.Manifest
import android.os.Build

/**
 * Returns the list of Android runtime permissions the meeting room SDK requires,
 * conditional on the running API level.
 *
 * @param sdkInt Defaults to [Build.VERSION.SDK_INT]; injectable for testing.
 */
internal fun computeRequiredPermissions(sdkInt: Int = Build.VERSION.SDK_INT): List<String> =
    buildList {
        add(Manifest.permission.CAMERA)
        add(Manifest.permission.RECORD_AUDIO)
        // BLUETOOTH_CONNECT became a runtime permission in Android 12 (API 31, S).
        // The audio selector layer checks it for API 31+, so request it from that level.
        if (sdkInt >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        // POST_NOTIFICATIONS was introduced as a runtime permission in Android 13 (API 33).
        if (sdkInt >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
