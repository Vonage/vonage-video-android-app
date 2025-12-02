package com.vonage.android.screen.components.permissions

import android.Manifest
import android.os.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallPermissionHandler(
    onGrantPermissions: () -> Unit = {},
    navigateToPermissions: () -> Unit = {},
) {
    if (LocalInspectionMode.current) return

    val permissions = mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions += Manifest.permission.BLUETOOTH_CONNECT
        permissions += Manifest.permission.POST_NOTIFICATIONS
    }
    val state = rememberMultiplePermissionsState(permissions)
    LaunchedEffect(state) {
        state.launchMultiplePermissionRequest()
    }
    PermissionHandler(
        multiplePermissionsState = state,
        navigateToPermissions = navigateToPermissions,
        onGrantAllPermissions = onGrantPermissions,
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermissionHandler(
    multiplePermissionsState: MultiplePermissionsState,
    onGrantAllPermissions: () -> Unit = {},
    navigateToPermissions: () -> Unit = {},
) {
    if (multiplePermissionsState.allPermissionsGranted) {
        onGrantAllPermissions()
    } else {
        PermissionsDialog(
            multiplePermissionsState = multiplePermissionsState,
            navigateToPermissions = navigateToPermissions,
        )
    }
}
