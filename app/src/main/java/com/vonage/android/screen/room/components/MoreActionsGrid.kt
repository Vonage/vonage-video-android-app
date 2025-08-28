package com.vonage.android.screen.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ScreenShare
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.R
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.RecordingState

@Composable
fun MoreActionsGrid(
    recordingState: RecordingState,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    val actions = listOf(
        recordingAction(
            actions = actions,
            recordingState = recordingState,
        ),
        // rest of the actions are placeholders
        ExtraAction(
            id = 2,
            icon = Icons.AutoMirrored.Default.ScreenShare,
            label = "Screen share",
            isSelected = false,
            onClick = {},
        ),
        ExtraAction(
            id = 3,
            icon = Icons.Default.ClosedCaption,
            label = "Captions",
            isSelected = false,
            onClick = {},
        ),
        ExtraAction(
            id = 4,
            icon = Icons.Default.BugReport,
            label = "Report issue",
            isSelected = false,
            onClick = {},
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

@Composable
private fun recordingAction(
    actions: MeetingRoomActions,
    recordingState: RecordingState,
): ExtraAction =
    ExtraAction(
        id = 1,
        icon = Icons.Default.Archive,
        label = when (recordingState) {
            RecordingState.IDLE,
            RecordingState.STARTING,
            RecordingState.STOPPING -> stringResource(R.string.recording_start_recording)

            RecordingState.RECORDING -> stringResource(R.string.recording_stop_recording)
        },
        isSelected = when (recordingState) {
            RecordingState.IDLE,
            RecordingState.STOPPING -> false

            RecordingState.STARTING,
            RecordingState.RECORDING -> true
        },
        onClick = remember {
            {
                when (recordingState) {
                    RecordingState.IDLE -> actions.onToggleRecording(true)
                    RecordingState.RECORDING -> actions.onToggleRecording(false)
                    RecordingState.STARTING,
                    RecordingState.STOPPING -> {
                    }
                }
            }
        },
    )

@Composable
private fun ActionCell(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClickCell: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val defaultColor = VonageVideoTheme.colors.inverseSurface

    Column(
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClickCell)
            .conditional(
                isSelected,
                ifTrue = {
                    background(VonageVideoTheme.colors.primary, RoundedCornerShape(8.dp))
                },
                ifFalse = {
                    border(1.dp, MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(8.dp))
                },
            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = defaultColor,
            modifier = Modifier.size(24.dp),
        )

        Text(
            text = label,
            color = defaultColor,
            style = VonageVideoTheme.typography.body,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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
            modifier = Modifier.background(VonageVideoTheme.colors.surface),
            recordingState = RecordingState.RECORDING,
            actions = MeetingRoomActions(),
        )
    }
}
