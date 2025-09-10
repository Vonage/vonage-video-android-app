package com.vonage.android.screen.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.components.ActionCell
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.room.CaptionsState
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.RecordingState
import com.vonage.android.screen.room.ScreenSharingState
import com.vonage.android.screen.room.components.captions.captionsAction
import com.vonage.android.screen.room.components.recording.recordingAction
import com.vonage.android.screen.reporting.components.reportingAction
import com.vonage.android.screensharing.ui.screenSharingAction

@Composable
fun MoreActionsGrid(
    recordingState: RecordingState,
    screenSharingState: ScreenSharingState,
    captionsState: CaptionsState,
    actions: MeetingRoomActions,
    onShowReporting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val actions = listOf(
        recordingAction(
            actions = actions,
            recordingState = recordingState,
        ),
        screenSharingAction(
            actions = actions,
            screenSharingState = screenSharingState,
        ),
        captionsAction(
            actions = actions,
            captionsState = captionsState,
        ),
        reportingAction(
            onClick = onShowReporting,
        ),
    )

    LazyVerticalGrid(
        modifier = modifier.padding(bottom = 24.dp),
        contentPadding = PaddingValues(8.dp),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = actions,
            key = { action -> action.id },
        ) { action ->
            ActionCell(
                icon = action.icon,
                label = action.label,
                isSelected = action.isSelected,
                onClickCell = action.onClick,
            )
        }
    }
}

data class ExtraAction(
    val id: Int,
    val icon: ImageVector,
    val label: String,
    val isSelected: Boolean,
    val onClick: () -> Unit,
)

@PreviewLightDark
@Composable
internal fun MoreActionsGridPreview() {
    VonageVideoTheme {
        MoreActionsGrid(
            recordingState = RecordingState.RECORDING,
            screenSharingState = ScreenSharingState.SHARING,
            captionsState = CaptionsState.ENABLED,
            actions = MeetingRoomActions(),
            modifier = Modifier.background(VonageVideoTheme.colors.surface),
            onShowReporting = {},
        )
    }
}
