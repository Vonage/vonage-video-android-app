package com.vonage.android.screensharing.ui

import androidx.compose.runtime.Composable
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.BottomBarActionType
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.ScreenShare
import com.vonage.android.screensharing.ScreenSharingState
import com.vonage.android.screensharing.ScreenSharingState.IDLE
import com.vonage.android.screensharing.ScreenSharingState.SHARING
import com.vonage.android.screensharing.ScreenSharingState.STARTING
import com.vonage.android.screensharing.ScreenSharingState.STOPPING

@Composable
fun screenSharingAction(
    onStartScreenSharing: () -> Unit,
    onStopScreenSharing: () -> Unit,
    startScreenSharingLabel: String,
    stopScreenSharingLabel: String,
    screenSharingState: ScreenSharingState,
): BottomBarAction? =
    BottomBarAction(
        type = BottomBarActionType.SCREEN_SHARING,
        icon = when (screenSharingState) {
            STOPPING,
            IDLE,
            STARTING,
            SHARING -> VividIcons.Solid.ScreenShare
        },
        label = when (screenSharingState) {
            IDLE,
            STARTING,
            STOPPING -> startScreenSharingLabel

            SHARING -> stopScreenSharingLabel
        },
        isSelected = when (screenSharingState) {
            IDLE,
            STOPPING -> false

            STARTING,
            SHARING -> true
        },
        onClick = {
            when (screenSharingState) {
                IDLE -> onStartScreenSharing()
                SHARING -> onStopScreenSharing()
                else -> {}
            }
        },
    )
