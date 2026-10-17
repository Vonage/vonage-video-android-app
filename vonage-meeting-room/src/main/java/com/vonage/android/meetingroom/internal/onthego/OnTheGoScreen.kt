package com.vonage.android.meetingroom.internal.onthego

import android.content.res.Configuration
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vonage.android.compose.components.ParticipantVideoRenderer
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.Close
import com.vonage.android.compose.vivid.icons.solid.AudioMid
import com.vonage.android.compose.vivid.icons.solid.EndCall
import com.vonage.android.compose.vivid.icons.solid.MicMute
import com.vonage.android.compose.vivid.icons.solid.Microphone2
import com.vonage.android.compose.vivid.icons.solid.ScreenShare
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.meetingroom.R

internal data class OnTheGoScreenActions(
    val onToggleMic: () -> Unit,
    val onExit: () -> Unit,
    val onLeaveCall: () -> Unit,
    val onOpenAudioRoute: () -> Unit,
    val onPresentSharedContent: () -> Unit,
    val onDismissSharedContent: () -> Unit,
)

@Composable
internal fun OnTheGoScreen(
    snapshot: OnTheGoCallSnapshot,
    sharedParticipant: Participant?,
    sharedContentPresented: Boolean,
    actions: OnTheGoScreenActions,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    var skipMicAnnouncement by remember { mutableStateOf(true) }
    val micOnAnnouncement = stringResource(R.string.on_the_go_mic_on_announcement)
    val micOffAnnouncement = stringResource(R.string.on_the_go_mic_off_announcement)
    LaunchedEffect(snapshot.isMicEnabled) {
        if (skipMicAnnouncement) {
            skipMicAnnouncement = false
        } else {
            view.announceForAccessibility(
                if (snapshot.isMicEnabled) micOnAnnouncement else micOffAnnouncement,
            )
        }
    }

    val onMic = {
        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        actions.onToggleMic()
    }
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VonageVideoTheme.colors.background)
            .testTag(OnTheGoTestTags.SCREEN),
    ) {
        if (landscape) {
            LandscapeOnTheGo(snapshot = snapshot, actions = actions, onMic = onMic)
        } else {
            PortraitOnTheGo(snapshot = snapshot, actions = actions, onMic = onMic)
        }
    }

    if (sharedContentPresented && sharedParticipant != null) {
        SharedContentDialog(participant = sharedParticipant, onDone = actions.onDismissSharedContent)
    }
}

@Composable
private fun PortraitOnTheGo(
    snapshot: OnTheGoCallSnapshot,
    actions: OnTheGoScreenActions,
    onMic: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(VonageVideoTheme.dimens.paddingLarge),
    ) {
        Header(snapshot = snapshot, onExit = actions.onExit)
        Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceDefault))
        SpeakerLine(speaker = snapshot.activeSpeakerName)
        MuteButton(
            isMicEnabled = snapshot.isMicEnabled,
            onClick = onMic,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = VonageVideoTheme.dimens.paddingLarge),
        )
        if (snapshot.screenShare != null) {
            SharedContentRow(onClick = actions.onPresentSharedContent)
            Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceMedium))
        }
        BottomActions(onOpenAudioRoute = actions.onOpenAudioRoute, onLeaveCall = actions.onLeaveCall)
    }
}

@Composable
private fun LandscapeOnTheGo(
    snapshot: OnTheGoCallSnapshot,
    actions: OnTheGoScreenActions,
    onMic: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(VonageVideoTheme.dimens.paddingLarge),
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceDefault),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            Header(snapshot = snapshot, onExit = actions.onExit)
            Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceMedium))
            SpeakerLine(speaker = snapshot.activeSpeakerName)
            Spacer(modifier = Modifier.weight(1f))
            if (snapshot.screenShare != null) {
                SharedContentRow(onClick = actions.onPresentSharedContent)
                Spacer(modifier = Modifier.height(VonageVideoTheme.dimens.spaceSmall))
            }
            BottomActions(onOpenAudioRoute = actions.onOpenAudioRoute, onLeaveCall = actions.onLeaveCall)
        }
        MuteButton(isMicEnabled = snapshot.isMicEnabled, onClick = onMic, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun Header(snapshot: OnTheGoCallSnapshot, onExit: () -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.on_the_go_title),
                style = VonageVideoTheme.typography.bodyExtendedSemibold,
                color = VonageVideoTheme.colors.textPrimary,
            )
            Text(
                text = stringResource(R.string.on_the_go_participant_count, snapshot.participantCount),
                style = VonageVideoTheme.typography.caption,
                color = VonageVideoTheme.colors.textSecondary,
            )
        }
        ExitButton(onExit)
    }
}

@Composable
private fun ExitButton(onExit: () -> Unit) {
    val label = stringResource(R.string.on_the_go_exit_description)
    Row(
        modifier = Modifier
            .semantics { contentDescription = label }
            .clip(VonageVideoTheme.shapes.extraLarge)
            .border(
                width = VonageVideoTheme.dimens.borderWidthDefault,
                color = VonageVideoTheme.colors.textPrimary,
                shape = VonageVideoTheme.shapes.extraLarge,
            )
            .clickable(onClick = onExit)
            .padding(
                horizontal = VonageVideoTheme.dimens.paddingMedium,
                vertical = VonageVideoTheme.dimens.paddingSmall,
            )
            .testTag(OnTheGoTestTags.EXIT),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceXSmall),
    ) {
        Icon(
            imageVector = VividIcons.Line.Close,
            contentDescription = null,
            tint = VonageVideoTheme.colors.textPrimary,
        )
        Text(
            text = stringResource(R.string.on_the_go_exit),
            style = VonageVideoTheme.typography.captionSemibold,
            color = VonageVideoTheme.colors.textPrimary,
        )
    }
}

@Composable
private fun SpeakerLine(speaker: String?) {
    Row(
        modifier = Modifier.testTag(OnTheGoTestTags.SPEAKER),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
    ) {
        Box(
            modifier = Modifier
                .size(VonageVideoTheme.dimens.spaceSmall)
                .clip(CircleShape)
                .background(
                    if (speaker != null) VonageVideoTheme.colors.success else VonageVideoTheme.colors.textSecondary,
                ),
        )
        Text(
            text = if (speaker != null) {
                stringResource(R.string.on_the_go_speaker_speaking, speaker)
            } else {
                stringResource(R.string.on_the_go_no_one_speaking)
            },
            style = VonageVideoTheme.typography.bodyBase,
            color = if (speaker != null) VonageVideoTheme.colors.textPrimary else VonageVideoTheme.colors.textSecondary,
        )
    }
}

@Composable
private fun MuteButton(
    isMicEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val container = if (isMicEnabled) VonageVideoTheme.colors.primary else VonageVideoTheme.colors.surface
    val content = if (isMicEnabled) VonageVideoTheme.colors.onPrimary else VonageVideoTheme.colors.error
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(VonageVideoTheme.shapes.extraLarge)
            .background(container)
            .then(
                if (isMicEnabled) {
                    Modifier
                } else {
                    Modifier.border(
                        width = VonageVideoTheme.dimens.borderWidthThick,
                        color = VonageVideoTheme.colors.error,
                        shape = VonageVideoTheme.shapes.extraLarge,
                    )
                },
            )
            .clickable(onClick = onClick)
            .testTag(OnTheGoTestTags.MUTE),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = if (isMicEnabled) VividIcons.Solid.Microphone2 else VividIcons.Solid.MicMute,
            contentDescription = stringResource(
                if (isMicEnabled) R.string.on_the_go_mic_on_description else R.string.on_the_go_mic_off_description,
            ),
            tint = content,
            modifier = Modifier.size(VonageVideoTheme.dimens.spaceXXXLarge),
        )
        Text(
            text = stringResource(if (isMicEnabled) R.string.on_the_go_microphone_on else R.string.on_the_go_microphone_off),
            style = VonageVideoTheme.typography.captionSemibold,
            color = content,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(if (isMicEnabled) R.string.on_the_go_tap_to_mute else R.string.on_the_go_tap_to_unmute),
            style = VonageVideoTheme.typography.bodyExtendedSemibold,
            color = if (isMicEnabled) VonageVideoTheme.colors.onPrimary else VonageVideoTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SharedContentRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VonageVideoTheme.shapes.large)
            .background(VonageVideoTheme.colors.surface)
            .border(
                width = VonageVideoTheme.dimens.borderWidthThin,
                color = VonageVideoTheme.colors.primary,
                shape = VonageVideoTheme.shapes.large,
            )
            .clickable(onClick = onClick)
            .padding(VonageVideoTheme.dimens.paddingMedium)
            .testTag(OnTheGoTestTags.SHARED_CONTENT),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall, Alignment.CenterHorizontally),
    ) {
        Icon(imageVector = VividIcons.Solid.ScreenShare, contentDescription = null, tint = VonageVideoTheme.colors.primary)
        Text(
            text = stringResource(R.string.on_the_go_view_shared_content),
            style = VonageVideoTheme.typography.bodyBaseSemibold,
            color = VonageVideoTheme.colors.primary,
        )
        Text(
            text = stringResource(R.string.on_the_go_live),
            style = VonageVideoTheme.typography.captionSemibold,
            color = VonageVideoTheme.colors.onError,
            modifier = Modifier
                .clip(VonageVideoTheme.shapes.small)
                .background(VonageVideoTheme.colors.error)
                .padding(horizontal = VonageVideoTheme.dimens.paddingXSmall),
        )
    }
}

@Composable
private fun BottomActions(onOpenAudioRoute: () -> Unit, onLeaveCall: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall)) {
        ActionChip(
            label = stringResource(R.string.audio_device_selector_speaker_audio_device),
            contentDescription = stringResource(R.string.audio_device_selector_change_audio_output),
            icon = VividIcons.Solid.AudioMid,
            container = VonageVideoTheme.colors.surface,
            content = VonageVideoTheme.colors.textSecondary,
            onClick = onOpenAudioRoute,
            modifier = Modifier
                .weight(1f)
                .testTag(OnTheGoTestTags.AUDIO_ROUTE),
        )
        ActionChip(
            label = stringResource(R.string.on_the_go_leave),
            contentDescription = stringResource(R.string.on_the_go_leave_call),
            icon = VividIcons.Solid.EndCall,
            container = VonageVideoTheme.colors.error,
            content = VonageVideoTheme.colors.onError,
            onClick = onLeaveCall,
            modifier = Modifier
                .weight(1f)
                .testTag(OnTheGoTestTags.LEAVE),
        )
    }
}

@Composable
private fun ActionChip(
    label: String,
    contentDescription: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    container: androidx.compose.ui.graphics.Color,
    content: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(VonageVideoTheme.shapes.large)
            .background(container)
            .clickable(onClick = onClick)
            .padding(VonageVideoTheme.dimens.paddingMedium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = content)
        Spacer(modifier = Modifier.width(VonageVideoTheme.dimens.spaceSmall))
        Text(text = label, style = VonageVideoTheme.typography.bodyBaseSemibold, color = content)
    }
}

@Composable
private fun SharedContentDialog(participant: Participant, onDone: () -> Unit) {
    Dialog(
        onDismissRequest = onDone,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VonageVideoTheme.colors.background)
                .testTag(OnTheGoTestTags.SHARED_SHEET),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(VonageVideoTheme.dimens.paddingDefault),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = stringResource(R.string.on_the_go_done),
                    style = VonageVideoTheme.typography.bodyBaseSemibold,
                    color = VonageVideoTheme.colors.primary,
                    modifier = Modifier
                        .clickable(onClick = onDone)
                        .padding(VonageVideoTheme.dimens.paddingSmall)
                        .testTag(OnTheGoTestTags.SHARED_DONE),
                )
            }
            ParticipantVideoRenderer(participant = participant, modifier = Modifier.fillMaxSize())
        }
    }
}

internal object OnTheGoTestTags {
    const val SCREEN = "on-the-go-screen"
    const val EXIT = "on-the-go-exit"
    const val SPEAKER = "on-the-go-speaker"
    const val MUTE = "on-the-go-mute"
    const val SHARED_CONTENT = "on-the-go-shared-content"
    const val SHARED_SHEET = "on-the-go-shared-sheet"
    const val SHARED_DONE = "on-the-go-shared-done"
    const val AUDIO_ROUTE = "on-the-go-audio-route"
    const val LEAVE = "on-the-go-leave"
    const val BANNER = "on-the-go-banner"
    const val SWITCH = "on-the-go-switch"
    const val NOT_NOW = "on-the-go-not-now"
    const val MENU = "on-the-go-menu"
    const val SETTINGS_TOGGLE = "on-the-go-suggest-when-moving"
}
