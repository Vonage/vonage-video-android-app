package com.vonage.android.meetingroom.internal.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.meetingroom.R

/** Error dialog shown when the session could not be created or failed; offers retry or leaving the room. */
@Composable
internal fun SessionErrorDialog(
    uiState: MeetingRoomUiState,
    actions: MeetingRoomActions,
) {
    val message = when {
        uiState.isSessionKeyExpired -> stringResource(R.string.meeting_screen_session_key_expired_error)
        uiState.errorMessage?.isNotBlank() == true -> uiState.errorMessage
        else -> stringResource(R.string.meeting_screen_session_creation_error)
    }
    BasicAlertDialog(
        text = message,
        acceptLabel = stringResource(R.string.generic_retry),
        onAccept = actions.onRetry,
        onCancel = actions.onBack,
    )
}
