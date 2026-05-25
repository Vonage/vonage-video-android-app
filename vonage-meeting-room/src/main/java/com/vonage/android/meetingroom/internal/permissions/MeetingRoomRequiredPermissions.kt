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
        if (sdkInt >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
            add(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
