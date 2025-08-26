package com.vonage.android.screen.room.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ScreenShare
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.screen.room.MeetingRoomActions

data class ExtraAction(
    val id: Int,
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)

@Composable
fun MoreActionsGrid(
    isRecording: Boolean,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    val actions = listOf(
        ExtraAction(
            id = 1,
            icon = Icons.Default.Archive,
            label = "Record",
            onClick = {
                //actions.onToggleRecording(isRecording.toggle(), "")
                actions.onToggleRecording(true, "")
            },
        ),
        ExtraAction(
            id = 2,
            icon = Icons.AutoMirrored.Default.ScreenShare,
            label = "Screen share",
            onClick = {},
        ),
        ExtraAction(
            id = 3,
            icon = Icons.Default.ClosedCaption,
            label = "Captions",
            onClick = {},
        ),
        ExtraAction(
            id = 4,
            icon = Icons.Default.BugReport,
            label = "Report issue",
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
                onClickCell = action.onClick,
            )
        }
    }
}

@Composable
private fun ActionCell(
    icon: ImageVector,
    label: String,
    onClickCell: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val defaultColor = VonageVideoTheme.colors.inverseSurface

    Column(
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClickCell)
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

@PreviewLightDark
@Composable
internal fun MoreActionsGridPreview() {
    VonageVideoTheme {
        MoreActionsGrid(
            isRecording = false,
            actions = MeetingRoomActions(),
        )
    }
}
