package com.vonage.android.screen.room

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.compose.components.BasicAlertDialog
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.kotlin.model.EmojiState
import com.vonage.android.kotlin.model.SignalType
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_BOTTOM_BAR
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_CONTENT
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_TOP_BAR
import com.vonage.android.screen.room.components.BottomBar
import com.vonage.android.screen.room.components.BottomBarState
import com.vonage.android.screen.room.components.GenericLoading
import com.vonage.android.screen.room.components.MeetingRoomContent
import com.vonage.android.screen.room.components.ParticipantVideoCard
import com.vonage.android.screen.room.components.TopBar
import com.vonage.android.screen.room.components.chat.ChatPanel
import com.vonage.android.screen.room.components.emoji.EmojiReactionOverlay
import com.vonage.android.util.ext.isExtraPaneShow
import com.vonage.android.util.ext.toggleChat
import com.vonage.android.util.preview.buildCallWithParticipants
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

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
