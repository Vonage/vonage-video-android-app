package com.vonage.android.meetingroom.components.bottombar

import androidx.compose.runtime.Composable
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.BottomBarActionType
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Group2

@Composable
internal fun participantsAction(
    participantsCount: Int,
    onToggleParticipants: () -> Unit,
): BottomBarAction =
    BottomBarAction(
        type = BottomBarActionType.PARTICIPANTS,
        icon = VividIcons.Solid.Group2,
        label = "Participants",
        badgeCount = participantsCount,
        onClick = onToggleParticipants,
    )
