package com.vonage.android.screen.room

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.screen.room.components.ChatBadgeButton
import com.vonage.android.screen.room.components.GenericLoading
import com.vonage.android.screen.room.components.ParticipantVideoCard
import com.vonage.android.util.pip.findActivity
import com.vonage.android.util.preview.buildCallWithParticipants

@Suppress("LongMethod")
@Composable
fun PipMeetingRoomScreen(
    uiState: MeetingRoomUiState,
    actions: MeetingRoomActions,
    audioLevel: Float,
    modifier: Modifier = Modifier,
) {
    when {
        (uiState.isError.not() && uiState.isLoading.not() && uiState.isEndCall.not()) -> {
            val participants by uiState.call.participantsStateFlow.collectAsStateWithLifecycle()
            val signalState by uiState.call.signalStateFlow.collectAsStateWithLifecycle(null)
            val chatState = signalState?.signals[SignalType.CHAT.signal] as? ChatState
            val participant = participants.last() // replace to active speaker when logic available

            Box(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                ParticipantVideoCard(
                    name = participant.name,
                    isCameraEnabled = participant.isCameraEnabled,
                    isMicEnabled = participant.isMicEnabled,
                    view = participant.view,
                    audioLevel = audioLevel,
                    isSpeaking = participant.isSpeaking,
                    isShowVolumeIndicator = participant is VeraPublisher,
                )
                ChatBadgeButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    unreadCount = chatState?.unreadCount ?: 0,
                    onShowChat = {},
                    isChatShow = true,
                )
            }
        }

        (uiState.isLoading) -> GenericLoading()

        (uiState.isError) -> {
            BasicAlertDialog(
                text = stringResource(R.string.meeting_screen_session_creation_error),
                acceptLabel = stringResource(R.string.generic_retry),
                onAccept = actions.onRetry,
                onCancel = actions.onBack,
            )
        }

        (uiState.isEndCall) -> {
            LaunchedEffect(uiState) {
                actions.onEndCall()
            }
            LocalContext.current.findActivity().finish()
        }
    }
}

@PreviewLightDark
@Composable
internal fun PipMeetingRoomScreenSessionPreview() {
    VonageVideoTheme {
        PipMeetingRoomScreen(
            uiState = MeetingRoomUiState(
                roomName = "sample-room-name",
                call = buildCallWithParticipants(1),
            ),
            actions = MeetingRoomActions(),
            audioLevel = 0.5f,
        )
    }
}
