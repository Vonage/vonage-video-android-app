package com.vonage.android.meetingroom.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.components.VonageTopAppBar
import com.vonage.android.compose.icons.AudioSelectorIcon
import com.vonage.android.compose.icons.CameraSwitchIcon
import com.vonage.android.compose.icons.ShareIcon
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.meetingroom.LocalMeetingRoomPlugins
import com.vonage.android.meetingroom.MeetingRoomActions
import com.vonage.android.meetingroom.R
import com.vonage.android.meetingroom.components.TopBarTestTags.TOP_BAR_AUDIO_SELECTOR_ACTION
import com.vonage.android.meetingroom.components.TopBarTestTags.TOP_BAR_CAMERA_SWITCH_ACTION
import com.vonage.android.meetingroom.components.TopBarTestTags.TOP_BAR_SHARE_ACTION
import com.vonage.android.meetingroom.components.TopBarTestTags.TOP_BAR_TITLE
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.AudioMid
import com.vonage.android.compose.vivid.icons.solid.Call
import com.vonage.android.compose.vivid.icons.solid.Headset2
import com.vonage.audioselector.AudioDeviceSelector.AudioDeviceType
import com.vonage.audioselector.AudioDevicesState
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Top bar for the meeting room.
 *
 * Plugin contributions:
 * - [titleDecoration] — content rendered before the room name (e.g. recording dot).
 * - [extraActions] — icon buttons appended to the default action row (e.g. settings gear).
 *
 * @param onToggleAudioDeviceSelector When null the audio selector icon is not shown.
 */
@Composable
fun MeetingTopBar(
    roomName: String,
    audioDevicesState: AudioDevicesState?,
    actions: MeetingRoomActions,
    onToggleAudioDeviceSelector: (() -> Unit)?,
    modifier: Modifier = Modifier,
    titleDecoration: @Composable RowScope.() -> Unit = {
        val plugins = LocalMeetingRoomPlugins.current
        plugins.forEach { it.TopBarTitleDecoration() }
    },
    extraActions: @Composable RowScope.() -> Unit = {
        val plugins = LocalMeetingRoomPlugins.current
        plugins.forEach { it.TopBarActions(actions) }
    },
) {
    VonageTopAppBar(
        modifier = modifier,
        onBack = actions.onBack,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                titleDecoration()
                Text(
                    modifier = Modifier.testTag(TOP_BAR_TITLE),
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
                modifier = Modifier.testTag(TOP_BAR_CAMERA_SWITCH_ACTION),
                onClick = actions.onCameraSwitch,
            ) {
                CameraSwitchIcon()
            }

            if (audioDevicesState != null && onToggleAudioDeviceSelector != null) {
                val activeDevice by audioDevicesState.activeDevice.collectAsStateWithLifecycle()
                IconButton(
                    modifier = Modifier.testTag(TOP_BAR_AUDIO_SELECTOR_ACTION),
                    onClick = onToggleAudioDeviceSelector,
                ) {
                    activeDevice?.let {
                        Icon(
                            imageVector = it.type.toImageVector(),
                            contentDescription = null,
                            tint = VonageVideoTheme.colors.onSurface,
                            modifier = Modifier.size(24.dp),
                        )
                    } ?: AudioSelectorIcon()
                }
            }

            IconButton(
                modifier = Modifier.testTag(TOP_BAR_SHARE_ACTION),
                onClick = { actions.onShare(roomName) },
            ) {
                ShareIcon(
                    contentDescription = stringResource(R.string.meeting_room_share_room_link)
                )
            }

            extraActions()
        },
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
            audioDevicesState = AudioDevicesState(
                availableDevices = MutableStateFlow(kotlinx.collections.immutable.persistentListOf()),
                activeDevice = MutableStateFlow(null),
                selectDevice = {},
            ),
            actions = MeetingRoomActions(),
            onToggleAudioDeviceSelector = {},
        )
    }
}

internal fun AudioDeviceType.toImageVector(): androidx.compose.ui.graphics.vector.ImageVector =
    when (this) {
        AudioDeviceType.EARPIECE -> VividIcons.Solid.Call
        AudioDeviceType.SPEAKER -> VividIcons.Solid.AudioMid
        AudioDeviceType.BLUETOOTH -> Icons.Filled.Bluetooth
        AudioDeviceType.WIRED_HEADSET -> VividIcons.Solid.Headset2
    }
