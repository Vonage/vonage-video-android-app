package com.vonage.android.plugins

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.meetingroom.MeetingRoomActions
import com.vonage.android.meetingroom.MeetingRoomUiPlugin
import com.vonage.android.screensharing.ScreenSharingState
import com.vonage.android.screensharing.ui.screenSharingAction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow

/**
 * [MeetingRoomUiPlugin] for the screen sharing feature.
 *
 * Provides:
 * - Bottom-bar action to start/stop screen sharing.
 *
 * @param screenSharingState  Live screen sharing state exposed by the ViewModel.
 */
class ScreenSharingUiPlugin(
    private val screenSharingState: StateFlow<ScreenSharingState>,
) : MeetingRoomUiPlugin {

    @Composable
    override fun bottomBarActions(
        actions: MeetingRoomActions,
        isPanelOpen: Boolean,
        onTogglePanel: () -> Unit,
    ): ImmutableList<BottomBarAction> {
        val state by screenSharingState.collectAsStateWithLifecycle()
        return listOfNotNull(
            screenSharingAction(
                onStartScreenSharing = { actions.onToggleScreenSharing(true) },
                onStopScreenSharing = { actions.onToggleScreenSharing(false) },
                startScreenSharingLabel = stringResource(R.string.screen_share_start),
                stopScreenSharingLabel = stringResource(R.string.screen_share_stop),
                screenSharingState = state,
            )
        ).toImmutableList()
    }
}
