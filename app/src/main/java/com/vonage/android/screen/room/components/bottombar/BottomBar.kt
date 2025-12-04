@file:OptIn(ExperimentalMaterial3Api::class)

package com.vonage.android.screen.room.components.bottombar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.compose.components.bottombar.ControlButton
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Chat2
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.reporting.ReportIssueScreen
import com.vonage.android.screen.reporting.components.reportingAction
import com.vonage.android.screen.room.CallLayoutType
import com.vonage.android.screen.room.CaptionsState
import com.vonage.android.screen.room.MeetingRoomActions
import com.vonage.android.screen.room.RecordingState
import com.vonage.android.screen.room.ScreenSharingState
import com.vonage.android.screen.room.components.captions.captionsAction
import com.vonage.android.screen.room.components.emoji.EmojiSelector
import com.vonage.android.screen.room.components.recording.recordingAction
import com.vonage.android.screen.room.noOpCallFacade
import com.vonage.android.screensharing.ui.screenSharingAction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@Stable
data class BottomBarState(
    val publisher: Participant?,
    val onShowChat: () -> Unit,
    val isChatShow: Boolean,
    val layoutType: CallLayoutType,
    val recordingState: RecordingState,
    val screenSharingState: ScreenSharingState,
    val captionsState: CaptionsState,
    val participants: ImmutableList<Participant>,
)

// 4 because mic + camera + menu + end
const val DEFAULT_ACTIONS_COUNT = 4

@Suppress("LongMethod")
@Composable
fun BottomBar(
    roomActions: MeetingRoomActions,
    call: CallFacade,
    state: BottomBarState,
    modifier: Modifier = Modifier,
    actions: ImmutableList<BottomBarActionType> = BottomBarActionType.entries.toImmutableList(),
) {
    val scope = rememberCoroutineScope()

    var showParticipants by remember { mutableStateOf(false) }
    val participantsSheetState = rememberModalBottomSheetState()
    var showMoreActions by remember { mutableStateOf(false) }
    val moreActionsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showReporting by remember { mutableStateOf(false) }
    val reportSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val density = LocalDensity.current
    val actionWidth = with(density) { VonageVideoTheme.dimens.minTouchTarget.toPx() }
    val spacingWidth = with(density) { VonageVideoTheme.dimens.spaceSmall.toPx() }
    val containerSpacing = with(density) { VonageVideoTheme.dimens.spaceXLarge.toPx() }

    var availableWidth by remember { mutableIntStateOf(0) }
    val pinnedActionsWidth = DEFAULT_ACTIONS_COUNT * actionWidth + (DEFAULT_ACTIONS_COUNT - 1) * spacingWidth
    val availableWidthForActions by remember(availableWidth) {
        derivedStateOf { (availableWidth - pinnedActionsWidth - containerSpacing).coerceAtLeast(0F) }
    }
    val actionsVisibleCount by remember(availableWidthForActions) {
        derivedStateOf { (availableWidthForActions / (actionWidth + spacingWidth)).toInt() }
    }

    val bottomBarActions = actionsFactory(
        actions = actions,
        state = state,
        roomActions = roomActions,
        call = call,
        onShowParticipants = {
            scope.launch {
                showParticipants = showParticipants.toggle()
                moreActionsSheetState.hide()
                showMoreActions = false
            }
        },
        onShowChat = {
            scope.launch {
                state.onShowChat()
                moreActionsSheetState.hide()
                showMoreActions = false
            }
        },
        onShowReporting = {
            scope.launch {
                showReporting = showReporting.toggle()
                moreActionsSheetState.hide()
                showMoreActions = false
            }
        }
    )
    val visibleActions = bottomBarActions.take(actionsVisibleCount)
    val overflowActions = bottomBarActions.drop(actionsVisibleCount)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .safeContentPadding()
            .onSizeChanged { size -> availableWidth = size.width },
        horizontalArrangement = Arrangement.Center,
    ) {
        CallControlBar(
            publisher = state.publisher,
            roomActions = roomActions,
            onShowMore = { showMoreActions = showMoreActions.toggle() },
        ) {
            visibleActions.forEach { action ->
                ControlButton(
                    icon = action.icon,
                    onClick = action.onClick,
                    badgeCount = action.badgeCount,
                    isActive = action.isSelected,
                )
            }
        }
    }

    if (showMoreActions) {
        ModalBottomSheet(
            onDismissRequest = { showMoreActions = false },
            sheetState = moreActionsSheetState,
        ) {
            EmojiSelector(
                onEmojiClick = { emoji -> roomActions.onEmojiSent(emoji) },
            )
            MoreActionsGrid(
                actions = overflowActions.toImmutableList(),
            )
        }
    }

    if (showParticipants) {
        ModalBottomSheet(
            onDismissRequest = { showParticipants = false },
            sheetState = participantsSheetState,
        ) {
            ParticipantsList(participants = state.participants)
        }
    }

    if (showReporting) {
        ModalBottomSheet(
            onDismissRequest = { showReporting = false },
            sheetState = reportSheetState,
        ) {
            ReportIssueScreen(
                onClose = {
                    scope.launch {
                        reportSheetState.hide()
                        showReporting = false
                    }
                },
            )
        }
    }
}

@Composable
private fun actionsFactory(
    actions: ImmutableList<BottomBarActionType>,
    state: BottomBarState,
    roomActions: MeetingRoomActions,
    call: CallFacade,
    onShowReporting: () -> Unit,
    onShowParticipants: () -> Unit,
    onShowChat: () -> Unit,
): ImmutableList<BottomBarAction> {
    val participantsCount by call.participantsCount.collectAsStateWithLifecycle()
    val chatState by call.chatSignalState.collectAsStateWithLifecycle()

    return actions.map { type ->
        when (type) {
            BottomBarActionType.CHANGE_LAYOUT -> layoutSelectorAction(
                layoutType = state.layoutType,
                roomActions = roomActions,
            )

            BottomBarActionType.PARTICIPANTS -> participantsAction(
                participantsCount = participantsCount,
                onToggleParticipants = onShowParticipants,
            )

            BottomBarActionType.CHAT -> BottomBarAction(
                type = BottomBarActionType.CHAT,
                icon = VividIcons.Solid.Chat2,
                label = stringResource(R.string.chat),
                isSelected = state.isChatShow,
                badgeCount = chatState?.unreadCount ?: 0,
                onClick = onShowChat,
            )

            BottomBarActionType.SCREEN_SHARING -> screenSharingAction(
                actions = roomActions,
                screenSharingState = state.screenSharingState,
            )

            BottomBarActionType.RECORD_SESSION -> recordingAction(
                actions = roomActions,
                recordingState = state.recordingState,
            )

            BottomBarActionType.CAPTIONS -> captionsAction(
                actions = roomActions,
                captionsState = state.captionsState,
            )

            BottomBarActionType.REPORT -> reportingAction(
                onClick = onShowReporting,
            )
        }
    }.toImmutableList()
}

object BottomBarTestTags {
    const val BOTTOM_BAR_PARTICIPANTS_BUTTON = "bottom_bar_participants_button"
    const val BOTTOM_BAR_PARTICIPANTS_BADGE = "bottom_bar_participants_badge"
    const val BOTTOM_BAR_END_CALL_BUTTON = "bottom_bar_end_call_button"
    const val BOTTOM_BAR_CAMERA_BUTTON = "bottom_bar_camera_button"
    const val BOTTOM_BAR_MIC_BUTTON = "bottom_bar_mic_button"
    const val BOTTOM_BAR_GRID_LAYOUT_BUTTON = "bottom_bar_grid_layout_button"
    const val BOTTOM_BAR_ACTIVE_SPEAKER_LAYOUT_BUTTON = "bottom_bar_active_speaker_layout_button"
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
internal fun BottomBarPreview() {
    VonageVideoTheme {
        BottomBar(
            roomActions = MeetingRoomActions(),
            call = noOpCallFacade,
            state = BottomBarState(
                publisher = buildParticipants(15).first(),
                participants = buildParticipants(15).toImmutableList(),
                onShowChat = {},
                isChatShow = false,
                layoutType = CallLayoutType.SPEAKER_LAYOUT,
                recordingState = RecordingState.RECORDING,
                captionsState = CaptionsState.IDLE,
                screenSharingState = ScreenSharingState.IDLE,
            ),
        )
    }
}
