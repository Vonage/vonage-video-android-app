package com.vonage.android.screen.room.components.captions

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vonage.android.R
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.ClosedCaptioningOff
import com.vonage.android.compose.vivid.icons.solid.ClosedCaptioning
import com.vonage.android.screen.room.CaptionsState
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.BottomBarActionType

@Composable
fun captionsAction(
    actions: MeetingRoomActions,
    captionsState: CaptionsState,
): BottomBarAction =
    BottomBarAction(
        type = BottomBarActionType.CAPTIONS,
        icon = when (captionsState) {
            CaptionsState.IDLE,
            CaptionsState.DISABLING -> VividIcons.Solid.ClosedCaptioning

            CaptionsState.ENABLING,
            CaptionsState.ENABLED -> VividIcons.Solid.ClosedCaptioningOff
        },
        label = when (captionsState) {
            CaptionsState.IDLE,
            CaptionsState.ENABLING,
            CaptionsState.DISABLING -> stringResource(R.string.captions_start)

            CaptionsState.ENABLED -> stringResource(R.string.captions_stop)
        },
        isSelected = when (captionsState) {
            CaptionsState.IDLE,
            CaptionsState.DISABLING -> false

            CaptionsState.ENABLING,
            CaptionsState.ENABLED -> true
        },
        onClick = {
            when (captionsState) {
                CaptionsState.IDLE -> actions.onToggleCaptions(true)
                CaptionsState.ENABLED -> actions.onToggleCaptions(false)
                CaptionsState.ENABLING,
                CaptionsState.DISABLING -> null
            }
        },
    )
