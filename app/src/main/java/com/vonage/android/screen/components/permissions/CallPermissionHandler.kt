package com.vonage.android.screen.components.permissions

import android.Manifest
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

    val state = rememberMultiplePermissionsState(
        mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH_CONNECT,
        )
    )
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
