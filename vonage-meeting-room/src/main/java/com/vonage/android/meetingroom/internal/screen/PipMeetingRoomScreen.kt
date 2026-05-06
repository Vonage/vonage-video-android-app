package com.vonage.android.meetingroom.internal.screen

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.chat.ui.ChatBadgeButton
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.compose.components.GenericLoading
import com.vonage.android.meetingroom.R
import com.vonage.android.meetingroom.internal.screen.components.ParticipantVideoCard
import com.vonage.android.meetingroom.internal.util.pip.findActivity

@Suppress("LongMethod")
@Composable
internal fun PipMeetingRoomScreen(
    uiState: MeetingRoomUiState,
    actions: MeetingRoomActions,
    modifier: Modifier = Modifier,
) {
    val call = uiState.call

    when {
        (uiState.isError.not() && uiState.isLoading.not() && uiState.isEndCall.not() && call != null) -> {
            val chatState by call.chatSignalState.collectAsStateWithLifecycle()
            val activeSpeakerParticipant by call.activeSpeaker.collectAsStateWithLifecycle()
            val publisher by call.publisher.collectAsStateWithLifecycle()

            Box(
                modifier = modifier.fillMaxWidth()
            ) {
                val participant = when (activeSpeakerParticipant) {
                    null -> publisher
                    else -> activeSpeakerParticipant
                }
                participant?.let { p ->
                    ParticipantVideoCard(
                        participant = p,
                        actions = actions,
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

        uiState.isLoading -> GenericLoading()

        uiState.isError -> {
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

        uiState.isEndCall -> {
            LaunchedEffect(uiState) {
                actions.onEndCall()
            }
            LocalContext.current.findActivity().finish()
        }
    }
}
