package com.vonage.android.meetingroom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.compose.components.GenericLoading
import com.vonage.android.compose.preview.buildCallWithParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.meetingroom.components.ParticipantVideoCard
import androidx.compose.ui.tooling.preview.PreviewLightDark

/**
 * Minimal meeting room screen shown when the app is in Picture-in-Picture mode.
 *
 * Feature-specific elements (e.g., chat unread badge) can be injected via
 * the [pipOverlay] slot to avoid creating a direct dependency on feature modules.
 *
 * @param pipOverlay  Optional composable rendered as a [BoxScope] overlay
 *                    (e.g. chat unread badge pinned to [Alignment.BottomEnd]).
 */
@Suppress("LongMethod")
@Composable
fun PipMeetingRoomScreen(
    uiState: MeetingRoomUiState,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
    pipOverlay: (@Composable androidx.compose.foundation.layout.BoxScope.() -> Unit)? = null,
) {
    when {
        (uiState.isError.not() && uiState.isLoading.not() && uiState.isEndCall.not()) -> {
            val activeSpeakerParticipant by uiState.call.activeSpeaker.collectAsStateWithLifecycle()
            val publisher by uiState.call.publisher.collectAsStateWithLifecycle()

            Box(modifier = modifier.fillMaxWidth()) {
                val participant = when (activeSpeakerParticipant) {
                    null -> publisher
                    else -> activeSpeakerParticipant
                }
                participant?.let {
                    ParticipantVideoCard(participant = it, actions = actions)
                }
                pipOverlay?.invoke(this)
            }
        }

        (uiState.isLoading) -> GenericLoading()

        (uiState.isError) -> {
            val message = if (uiState.errorMessage?.isNotBlank() == true) {
                uiState.errorMessage
            } else {
                stringResource(R.string.meeting_screen_session_creation_error)
            }
            BasicAlertDialog(
                text = message,
                acceptLabel = stringResource(R.string.generic_retry),
                onAccept = actions.onRetry,
                onCancel = actions.onBack,
            )
        }

        (uiState.isEndCall) -> {
            LaunchedEffect(uiState) {
                actions.onEndCall()
            }
        }
    }
}

@PreviewLightDark
@Composable
internal fun PipMeetingRoomScreenPreview() {
    VonageVideoTheme {
        PipMeetingRoomScreen(
            uiState = MeetingRoomUiState(
                roomName = "sample-room-name",
                call = buildCallWithParticipants(1),
            ),
            actions = MeetingRoomActions(),
        )
    }
}
