package com.vonage.android.screensharing.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ScreenShare
import androidx.compose.material.icons.automirrored.filled.StopScreenShare
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vonage.android.R
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.ScreenSharingState
import com.vonage.android.screen.room.ScreenSharingState.IDLE
import com.vonage.android.screen.room.ScreenSharingState.SHARING
import com.vonage.android.screen.room.ScreenSharingState.STARTING
import com.vonage.android.screen.room.ScreenSharingState.STOPPING
import com.vonage.android.screen.room.components.ExtraAction

@Composable
fun screenSharingAction(
    actions: MeetingRoomActions,
    screenSharingState: ScreenSharingState,
): ExtraAction =
    ExtraAction(
        id = 2,
        icon = when (screenSharingState) {
            STOPPING,
            IDLE -> Icons.AutoMirrored.Default.ScreenShare

            STARTING,
            SHARING -> Icons.AutoMirrored.Default.StopScreenShare
        },
        label = when (screenSharingState) {
            IDLE,
            STARTING,
            STOPPING -> stringResource(R.string.screen_share_start)

            SHARING -> stringResource(R.string.screen_share_stop)
        },
        isSelected = when (screenSharingState) {
            IDLE,
            STOPPING -> false

            STARTING,
            SHARING -> true
        },
        onClick = {
            when (screenSharingState) {
                IDLE -> actions.onToggleScreenSharing(true)
                SHARING -> actions.onToggleScreenSharing(false)
                STARTING,
                STOPPING -> {
                }
            }
        },
    )