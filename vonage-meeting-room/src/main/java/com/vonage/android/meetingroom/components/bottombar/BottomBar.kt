@file:OptIn(ExperimentalMaterial3Api::class)

package com.vonage.android.meetingroom.components.bottombar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.ControlButton
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.meetingroom.CallLayoutType
import com.vonage.android.meetingroom.MeetingRoomActions
import com.vonage.android.compose.preview.noOpCall
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

// 4 because mic + camera + menu + end
private const val DEFAULT_ACTIONS_COUNT = 4

/**
 * Bottom control bar for the meeting room.
 *
 * Core controls (mic, camera, end call, more) are always visible. Extra slots
 * ([pluginActions], [moreActionsContent]) are contributed by [MeetingRoomUiPlugin]
 * implementations so the bar itself has no direct feature dependencies.
 *
 * @param pluginActions   Actions contributed by plugins shown as icon buttons.
 * @param moreActionsContent  Content shown at the top of the "More actions" bottom sheet
 *                            (e.g. emoji selector, reporting form).
 */
@Suppress("LongMethod", "LongParameterList")
@Composable
fun BottomBar(
    call: CallFacade,
    roomActions: MeetingRoomActions,
    publisher: Participant?,
    participants: ImmutableList<Participant>,
    layoutType: CallLayoutType,
    allowShowParticipantList: Boolean,
    allowMicrophoneControl: Boolean,
    allowCameraControl: Boolean,
    pluginActions: ImmutableList<BottomBarAction> = persistentListOf(),
    moreActionsContent: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    var showParticipants by remember { mutableStateOf(false) }
    val participantsSheetState = rememberModalBottomSheetState()
    var showMoreActions by remember { mutableStateOf(false) }
    val moreActionsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val density = LocalDensity.current
    val actionWidth = with(density) { VonageVideoTheme.dimens.minTouchTarget.toPx() }
    val spacingWidth = with(density) { VonageVideoTheme.dimens.spaceSmall.toPx() }
    val containerSpacing = with(density) { VonageVideoTheme.dimens.spaceXLarge.toPx() }

    var availableWidth by remember { mutableIntStateOf(0) }
    val pinnedActionsWidth =
        DEFAULT_ACTIONS_COUNT * actionWidth + (DEFAULT_ACTIONS_COUNT - 1) * spacingWidth
    val availableWidthForActions by remember(availableWidth) {
        derivedStateOf { (availableWidth - pinnedActionsWidth - containerSpacing).coerceAtLeast(0F) }
    }
    val actionsVisibleCount by remember(availableWidthForActions) {
        derivedStateOf { (availableWidthForActions / (actionWidth + spacingWidth)).toInt() }
    }

    // Core actions always computed here; plugin actions are passed in
    val coreActions = buildCoreActions(
        layoutType = layoutType,
        roomActions = roomActions,
        participants = participants,
        call = call,
        allowShowParticipantList = allowShowParticipantList,
        onShowParticipants = {
            scope.launch {
                showParticipants = showParticipants.toggle()
                moreActionsSheetState.hide()
                showMoreActions = false
            }
        },
    )

    val allActions = (coreActions + pluginActions).toImmutableList()
    val visibleActions = allActions.take(actionsVisibleCount)
    val overflowActions = allActions.drop(actionsVisibleCount)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .safeContentPadding()
            .onSizeChanged { size -> availableWidth = size.width },
        horizontalArrangement = Arrangement.Center,
    ) {
        CallControlBar(
            publisher = publisher,
            roomActions = roomActions,
            allowMicrophoneControl = allowMicrophoneControl,
            allowCameraControl = allowCameraControl,
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
            moreActionsContent()
            MoreActionsGrid(actions = overflowActions.toImmutableList())
        }
    }

    if (showParticipants && allowShowParticipantList) {
        ModalBottomSheet(
            onDismissRequest = { showParticipants = false },
            sheetState = participantsSheetState,
        ) {
            val pinnedIds by call.pinnedParticipantIds.collectAsStateWithLifecycle()
            ParticipantsList(
                participants = participants,
                pinnedParticipantIds = pinnedIds,
                actions = roomActions,
            )
        }
    }
}

@Composable
private fun buildCoreActions(
    layoutType: CallLayoutType,
    roomActions: MeetingRoomActions,
    participants: ImmutableList<Participant>,
    call: CallFacade,
    allowShowParticipantList: Boolean,
    onShowParticipants: () -> Unit,
): List<BottomBarAction> {
    val participantsCount by call.participantsCount.collectAsStateWithLifecycle()
    return buildList {
        add(layoutSelectorAction(layoutType, roomActions))
        if (allowShowParticipantList) {
            add(participantsAction(participantsCount, onShowParticipants))
        }
    }
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
            call = noOpCall,
            publisher = buildParticipants(1).first(),
            participants = buildParticipants(15).toImmutableList(),
            layoutType = CallLayoutType.SPEAKER_LAYOUT,
            allowShowParticipantList = true,
            allowMicrophoneControl = true,
            allowCameraControl = true,
        )
    }
}
