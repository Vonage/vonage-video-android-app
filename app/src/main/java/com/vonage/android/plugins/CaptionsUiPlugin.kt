package com.vonage.android.plugins

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.captions.CaptionsUiState
import com.vonage.android.captions.ui.CaptionsOverlay
import com.vonage.android.captions.ui.captionsAction
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.meetingroom.MeetingRoomActions
import com.vonage.android.meetingroom.MeetingRoomUiPlugin
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow

/**
 * [MeetingRoomUiPlugin] for the captions feature.
 *
 * Provides:
 * - Captions text overlay on the video content.
 * - Bottom-bar action to enable/disable captions.
 *
 * @param captionsUiState  Live captions UI state exposed by the ViewModel.
 */
class CaptionsUiPlugin(
    private val captionsUiState: StateFlow<CaptionsUiState>,
) : MeetingRoomUiPlugin {

    @Composable
    override fun OverlayContent(call: CallFacade, modifier: androidx.compose.ui.Modifier) {
        val captionLines by call.captionsStateFlow.collectAsStateWithLifecycle()
        CaptionsOverlay(captionLines = captionLines, modifier = modifier)
    }

    @Composable
    override fun bottomBarActions(
        actions: MeetingRoomActions,
        isPanelOpen: Boolean,
        onTogglePanel: () -> Unit,
    ): ImmutableList<BottomBarAction> {
        val state by captionsUiState.collectAsStateWithLifecycle()
        return listOfNotNull(
            captionsAction(
                onEnableCaptions = { actions.onToggleCaptions(true) },
                onDisableCaptions = { actions.onToggleCaptions(false) },
                enableCaptionsLabel = stringResource(R.string.captions_start),
                disableCaptionsLabel = stringResource(R.string.captions_stop),
                captionsUiState = state,
            )
        ).toImmutableList()
    }
}
