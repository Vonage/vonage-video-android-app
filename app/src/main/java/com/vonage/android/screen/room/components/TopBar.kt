package com.vonage.android.screen.room.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.R
import com.vonage.android.compose.icons.AudioSelectorIcon
import com.vonage.android.compose.icons.CameraSwitchIcon
import com.vonage.android.compose.icons.ShareIcon
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_AUDIO_SELECTOR_ACTION
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_CAMERA_SWITCH_ACTION
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_SHARE_ACTION
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_TITLE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    roomName: String,
    actions: MeetingRoomActions,
    onToggleAudioDeviceSelector: () -> Unit,
    modifier: Modifier = Modifier,
) {

    TopAppBar(
        modifier = modifier,
        colors = topAppBarColors(
            containerColor = VonageVideoTheme.colors.surface,
        ),
        navigationIcon = {
            IconButton(
                onClick = actions.onBack,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        title = {
            Text(
                modifier = Modifier
                    .testTag(TOP_BAR_TITLE),
                text = roomName,
                style = VonageVideoTheme.typography.body,
                color = VonageVideoTheme.colors.inverseSurface,
            )
        },
        actions = {
            IconButton(
                modifier = Modifier
                    .testTag(TOP_BAR_CAMERA_SWITCH_ACTION),
                onClick = actions.onCameraSwitch,
            ) {
                CameraSwitchIcon()
            }
            IconButton(
                modifier = Modifier
                    .testTag(TOP_BAR_AUDIO_SELECTOR_ACTION),
                onClick = onToggleAudioDeviceSelector,
            ) {
                AudioSelectorIcon(
                    contentDescription = stringResource(R.string.audio_device_selector_change_audio_output)
                )
            }
            IconButton(
                modifier = Modifier
                    .testTag(TOP_BAR_SHARE_ACTION),
                onClick = { actions.onShare(roomName) },
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
    const val TOP_BAR_CAMERA_SWITCH_ACTION = "top_bar_camera_switch_action"
    const val TOP_BAR_AUDIO_SELECTOR_ACTION = "top_bar_audio_selector_action"
}

@PreviewLightDark
@Composable
internal fun TopBarPreview() {
    VonageVideoTheme {
        TopBar(
            roomName = "sample-name",
            actions = MeetingRoomActions(),
            onToggleAudioDeviceSelector = { },
        )
    }
}
