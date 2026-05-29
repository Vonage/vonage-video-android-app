package com.vonage.android.meetingroom.internal.screen.components.bottombar

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.BottomBarActionType
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Apps
import com.vonage.android.compose.vivid.icons.solid.Layout2
import com.vonage.android.meetingroom.R
import com.vonage.android.meetingroom.internal.screen.CallLayoutType
import com.vonage.android.meetingroom.internal.screen.MeetingRoomActions

@Composable
internal fun layoutSelectorAction(
    layoutType: CallLayoutType,
    roomActions: MeetingRoomActions,
): BottomBarAction =
    when (layoutType) {
        CallLayoutType.SPEAKER_LAYOUT -> BottomBarAction(
            type = BottomBarActionType.CHANGE_LAYOUT,
            icon = VividIcons.Solid.Layout2,
            label = stringResource(R.string.change_layout),
            onClick = { roomActions.onChangeLayout(CallLayoutType.ADAPTIVE_GRID) },
        )

        else -> BottomBarAction(
            type = BottomBarActionType.CHANGE_LAYOUT,
            icon = VividIcons.Solid.Apps,
            label = stringResource(R.string.change_layout),
            onClick = { roomActions.onChangeLayout(CallLayoutType.SPEAKER_LAYOUT) },
        )
    }
