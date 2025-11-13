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
import com.vonage.android.chat.ui.ChatBadgeButton
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.compose.preview.buildCallWithParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.screen.room.components.GenericLoading
import com.vonage.android.screen.room.components.ParticipantVideoCard
import com.vonage.android.util.pip.findActivity
import kotlinx.collections.immutable.persistentListOf

@Suppress("LongMethod")
@Composable
fun PipMeetingRoomScreen(
    uiState: MeetingRoomUiState,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    when {
        (uiState.isError.not() && uiState.isLoading.not() && uiState.isEndCall.not()) -> {
            val chatState by uiState.call.chatSignalState().collectAsStateWithLifecycle()
            val participants by uiState.call.participantsStateFlow.collectAsStateWithLifecycle(persistentListOf())
            val participant = participants.filterIsInstance<VeraPublisher>().firstOrNull()

            Box(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                participant?.let { participant ->
                    ParticipantVideoCard(
                        participant = participant,
                        isVolumeIndicatorVisible = participant is PublisherState,
                    )
                }
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
        )
    }
}
