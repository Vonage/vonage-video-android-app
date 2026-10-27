package com.vonage.android.meetingroom.internal.screen.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import com.vonage.android.compose.components.NotificationToast
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Rec
import com.vonage.android.compose.vivid.icons.solid.VoicemailTranscript
import com.vonage.android.meetingroom.R
import com.vonage.android.meetingroom.internal.screen.TranscriptionNotification
import kotlinx.coroutines.delay

private const val OVERLAY_ZINDEX = 11F
private const val OVERLAY_DURATION_MS = 4000L

/**
 * Recording and transcription toasts, stacked so both stay readable when the two archives
 * start together.
 */
@Composable
internal fun ArchiveNotificationOverlay(
    isRecordingStartedByOthers: Boolean,
    transcriptionNotification: TranscriptionNotification?,
    modifier: Modifier = Modifier,
) {
    var showRecordingToast by remember { mutableStateOf(false) }

    // Show overlay when recording starts, auto-dismiss after duration
    LaunchedEffect(isRecordingStartedByOthers) {
        if (isRecordingStartedByOthers) {
            showRecordingToast = true
            delay(OVERLAY_DURATION_MS)
            showRecordingToast = false
        }
    }

    Column(
        modifier = modifier
            .zIndex(OVERLAY_ZINDEX)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall, Alignment.Bottom),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = showRecordingToast,
            enter = toastEnter,
            exit = toastExit,
        ) {
            NotificationToast(
                icon = VividIcons.Solid.Rec,
                text = stringResource(R.string.recording_started_notification),
            )
        }
        // The view model clears the notification after its duration; AnimatedContent keeps the
        // previous toast on screen while it animates out.
        AnimatedContent(
            targetState = transcriptionNotification,
            transitionSpec = { toastEnter togetherWith toastExit },
            label = "transcription-notification",
        ) { notification ->
            if (notification != null) {
                NotificationToast(
                    icon = VividIcons.Solid.VoicemailTranscript,
                    text = stringResource(
                        when (notification) {
                            TranscriptionNotification.STARTED -> R.string.transcription_started_notification
                            TranscriptionNotification.STOPPED -> R.string.transcription_stopped_notification
                        },
                    ),
                )
            }
        }
    }
}

private val toastEnter = slideInVertically { -it } + fadeIn()
private val toastExit = slideOutVertically { -it } + fadeOut()
