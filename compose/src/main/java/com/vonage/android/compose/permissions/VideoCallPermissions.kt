package com.vonage.android.compose.permissions

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberCallPermissionsState(
    key: Any,
    permissions: List<String> =
        mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
        ),
    onPermissionsResult: ((Map<String, Boolean>) -> Unit)? = null,
    onAllPermissionsGranted: (suspend () -> Unit)? = null,
): PermissionsState {

    val permissionState = rememberMultiplePermissionsState(permissions) {
        onPermissionsResult?.invoke(it)
    }

    val allPermissionsGranted = permissionState.allPermissionsGranted
    LaunchedEffect(key1 = allPermissionsGranted) {
        if (allPermissionsGranted) {
            onAllPermissionsGranted?.invoke()
        }
    }

    return remember(key, permissions) {
        object : PermissionsState {
            override val allPermissionsGranted: Boolean
                get() = permissionState.allPermissionsGranted
            override val shouldShowRationale: Boolean
                get() = permissionState.shouldShowRationale

            override fun launchPermissionRequest() {
                permissionState.launchMultiplePermissionRequest()
            }
        }
    }
}

@Composable
fun LaunchVideoCallPermissions(
    key: Any,
    onPermissionsResult: ((Map<String, Boolean>) -> Unit)? = null,
    onAllPermissionsGranted: (suspend () -> Unit)? = null,
) {
    val callPermissionsState = rememberCallPermissionsState(
        key = key,
        onPermissionsResult = onPermissionsResult,
        onAllPermissionsGranted = onAllPermissionsGranted,
    )
    LaunchedEffect(key1 = key) { callPermissionsState.launchPermissionRequest() }
}
