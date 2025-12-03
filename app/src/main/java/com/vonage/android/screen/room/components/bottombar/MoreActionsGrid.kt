package com.vonage.android.screen.room.components.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.components.bottombar.ActionCell
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.reporting.components.reportingAction
import com.vonage.android.screen.room.CaptionsState
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.RecordingState
import com.vonage.android.screen.room.ScreenSharingState
import com.vonage.android.screen.room.components.captions.captionsAction
import com.vonage.android.screen.room.components.recording.recordingAction
import com.vonage.android.screensharing.ui.screenSharingAction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MoreActionsGrid(
    recordingState: RecordingState,
    screenSharingState: ScreenSharingState,
    captionsState: CaptionsState,
    roomActions: MeetingRoomActions,
    onShowReporting: () -> Unit,
    modifier: Modifier = Modifier,
    overflowActions: ImmutableList<BottomBarAction> = persistentListOf(),
) {
    val actions = listOf(
        recordingAction(
            actions = roomActions,
            recordingState = recordingState,
        ),
        screenSharingAction(
            actions = roomActions,
            screenSharingState = screenSharingState,
        ),
        captionsAction(
            actions = roomActions,
            captionsState = captionsState,
        ),
        reportingAction(
            onClick = onShowReporting,
        ),
    ) + overflowActions

    LazyVerticalGrid(
        modifier = modifier.padding(bottom = 24.dp),
        contentPadding = PaddingValues(8.dp),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = actions,
            key = { action -> action.type },
        ) { action ->
            ActionCell(
                icon = action.icon,
                label = action.label,
                isSelected = action.isSelected,
                onClickCell = action.onClick,
                badgeCount = action.badgeCount,
            )
        }
    }
}

@PreviewLightDark
@Composable
internal fun MoreActionsGridPreview() {
    VonageVideoTheme {
        MoreActionsGrid(
            recordingState = RecordingState.RECORDING,
            screenSharingState = ScreenSharingState.SHARING,
            captionsState = CaptionsState.ENABLED,
            roomActions = MeetingRoomActions(),
            modifier = Modifier.background(VonageVideoTheme.colors.surface),
            onShowReporting = {},
        )
    }
}
