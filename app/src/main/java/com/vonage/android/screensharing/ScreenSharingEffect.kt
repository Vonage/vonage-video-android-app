package com.vonage.android.screensharing

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun ScreenSharingEffect(
    onSuccess: (Intent) -> Unit,
) {
    val context = LocalContext.current
    val screenSharePermissionResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                onSuccess(it.data!!)
            }
        },
    )
    LaunchedEffect(Unit) {
        val mediaProjectionManager = context.getSystemService(MediaProjectionManager::class.java)
        screenSharePermissionResult.launch(mediaProjectionManager.createScreenCaptureIntent())
    }
}
