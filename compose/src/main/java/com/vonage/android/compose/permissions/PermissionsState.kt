package com.vonage.android.compose.permissions

import androidx.compose.runtime.Stable

@Stable
interface PermissionsState {

    val allPermissionsGranted: Boolean

    val shouldShowRationale: Boolean

    fun launchPermissionRequest()
}
