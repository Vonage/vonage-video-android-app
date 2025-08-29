package com.vonage.android.screen.room

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.screen.room.components.GenericLoading
import com.vonage.android.screen.room.components.ParticipantVideoCard
import com.vonage.android.util.preview.buildCallWithParticipants

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PipMeetingRoomScreen(
    uiState: MeetingRoomUiState,
    actions: MeetingRoomActions,
    audioLevel: Float,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is MeetingRoomUiState.Content -> {
            val participants by uiState.call.participantsStateFlow.collectAsStateWithLifecycle()
            val participant = participants.last() // replace to active speaker when logic available

            ParticipantVideoCard(
                modifier = modifier
                    .fillMaxWidth(),
                name = participant.name,
                isCameraEnabled = participant.isCameraEnabled,
                isMicEnabled = participant.isMicEnabled,
                view = participant.view,
                audioLevel = audioLevel,
                isSpeaking = participant.isSpeaking,
                isShowVolumeIndicator = participant is VeraPublisher,
            )
        }

        is MeetingRoomUiState.Loading -> GenericLoading()

        is MeetingRoomUiState.SessionError -> {
            BasicAlertDialog(
                text = stringResource(R.string.meeting_screen_session_creation_error),
                acceptLabel = stringResource(R.string.generic_retry),
                onAccept = actions.onRetry,
                onCancel = actions.onBack,
            )
        }

        is MeetingRoomUiState.EndCall -> {
            actions.onEndCall()
        }
    }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun PipMeetingRoomScreenSessionPreview() {
    VonageVideoTheme {
        PipMeetingRoomScreen(
            uiState = MeetingRoomUiState.Content(
                roomName = "sample-room-name",
                call = buildCallWithParticipants(1),
            ),
            actions = MeetingRoomActions(),
            audioLevel = 0.5f,
        )
    }
}
