package com.vonage.android.screensharing.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vonage.android.R
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.ScreenShareOff
import com.vonage.android.compose.vivid.icons.solid.ScreenShare
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.ScreenSharingState
import com.vonage.android.screen.room.ScreenSharingState.IDLE
import com.vonage.android.screen.room.ScreenSharingState.SHARING
import com.vonage.android.screen.room.ScreenSharingState.STARTING
import com.vonage.android.screen.room.ScreenSharingState.STOPPING
import com.vonage.android.screen.room.components.bottombar.BottomBarAction
import com.vonage.android.screen.room.components.bottombar.BottomBarActionType

@Composable
fun screenSharingAction(
    actions: MeetingRoomActions,
    screenSharingState: ScreenSharingState,
): BottomBarAction =
    BottomBarAction(
        type = BottomBarActionType.SCREEN_SHARING,
        icon = when (screenSharingState) {
            STOPPING,
            IDLE -> VividIcons.Solid.ScreenShare

            STARTING,
            SHARING -> VividIcons.Solid.ScreenShareOff
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
                else -> {}
            }
        },
    )