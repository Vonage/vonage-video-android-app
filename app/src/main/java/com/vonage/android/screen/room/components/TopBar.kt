package com.vonage.android.screen.room.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import com.vonage.android.R
import com.vonage.android.compose.icons.ShareIcon
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_SHARE_ACTION
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_TITLE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    roomName: String,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        title = {
            Text(
                modifier = Modifier
                    .testTag(TOP_BAR_TITLE),
                text = roomName,
                color = MaterialTheme.colorScheme.inverseSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        },
        actions = {
            IconButton(
                modifier = Modifier
                    .testTag(TOP_BAR_SHARE_ACTION),
                onClick = {
                    actions.onShare(roomName)
                }
            ) {
                ShareIcon(
                    contentDescription = stringResource(R.string.meeting_room_share_room_link)
                )
            }
        }
    )
}

object TopBarTestTags {
    const val TOP_BAR_TITLE = "top_bar_title"
    const val TOP_BAR_SHARE_ACTION = "top_bar_share_action"
}

@PreviewLightDark
@Composable
internal fun TopBarPreview() {
    VonageVideoTheme {
        TopBar(
            roomName = "sample-name",
            actions = MeetingRoomActions(),
        )
    }
}
