package com.vonage.android.screen.room.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.archiving.ArchivingUiState
import com.vonage.android.archiving.ui.RecordingIndicator
import com.vonage.android.audio.ui.rememberAudioDeviceSelector
import com.vonage.android.audio.ui.toImageVector
import com.vonage.android.compose.components.VonageTopAppBar
import com.vonage.android.compose.icons.AudioSelectorIcon
import com.vonage.android.compose.icons.CameraSwitchIcon
import com.vonage.android.compose.icons.ShareIcon
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_AUDIO_SELECTOR_ACTION
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_CAMERA_SWITCH_ACTION
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_SHARE_ACTION
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_TITLE

@Composable
fun MeetingTopBar(
    roomName: String,
    archivingUiState: ArchivingUiState,
    actions: MeetingRoomActions,
    onToggleAudioDeviceSelector: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val audioDeviceSelector = rememberAudioDeviceSelector(context)

    val activeDevice by audioDeviceSelector.activeDevice.collectAsStateWithLifecycle()

    VonageTopAppBar(
        modifier = modifier,
        onBack = actions.onBack,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (archivingUiState) {
                    ArchivingUiState.IDLE -> null
                    ArchivingUiState.STARTING,
                    ArchivingUiState.STOPPING -> CircularProgressIndicator(
                        color = Color.Red,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp)
                    )

                    ArchivingUiState.RECORDING -> RecordingIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp)
                    )
                }
                Text(
                    modifier = Modifier
                        .testTag(TOP_BAR_TITLE),
                    text = roomName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = VonageVideoTheme.typography.heading3,
                    color = VonageVideoTheme.colors.textSecondary,
                )
            }
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
                activeDevice?.let {
                    Icon(
                        imageVector = it.type.toImageVector(),
                        contentDescription = null,
                        tint = VonageVideoTheme.colors.onSurface,
                        modifier = Modifier.size(24.dp),
                    )
                } ?: AudioSelectorIcon(
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
internal fun MeetingTopBarPreview() {
    VonageVideoTheme {
        MeetingTopBar(
            roomName = "sample-name",
            archivingUiState = ArchivingUiState.RECORDING,
            actions = MeetingRoomActions(),
            onToggleAudioDeviceSelector = { },
        )
    }
}
