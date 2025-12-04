package com.vonage.android.screen.room.components.bottombar

import androidx.compose.runtime.Composable
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Group2

@Composable
fun participantsAction(
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
