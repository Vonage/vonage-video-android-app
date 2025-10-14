package com.vonage.android.screen.room.components.captions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vonage.android.R
import com.vonage.android.screen.room.CaptionsState
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.components.ExtraAction

@Composable
fun captionsAction(
    actions: MeetingRoomActions,
    captionsState: CaptionsState,
): ExtraAction =
    ExtraAction(
        id = 3,
        icon = Icons.Default.ClosedCaption,
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
