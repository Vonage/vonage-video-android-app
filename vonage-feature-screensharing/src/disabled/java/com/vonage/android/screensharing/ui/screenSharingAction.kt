package com.vonage.android.screensharing.ui

import androidx.compose.runtime.Composable
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.screensharing.ScreenSharingState

@Suppress("UnusedParameter", "FunctionOnlyReturningConstant")
@Composable
fun screenSharingAction(
    onStartScreenSharing: () -> Unit,
    onStopScreenSharing: () -> Unit,
    startScreenSharingLabel: String,
    stopScreenSharingLabel: String,
    screenSharingState: ScreenSharingState,
): BottomBarAction? = null
