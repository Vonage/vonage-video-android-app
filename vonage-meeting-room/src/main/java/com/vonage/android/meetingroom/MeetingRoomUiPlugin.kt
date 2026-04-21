package com.vonage.android.meetingroom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherParticipant
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Plugin interface for contributing UI to the meeting room screen.
 *
 * Feature modules implement this interface (in their flavor source sets) to inject
 * overlays, side-panel content, bottom-bar actions, emoji selectors, and top-bar
 * decorations without the meeting-room module needing to know about any feature.
 *
 * All methods have no-op default implementations so a plugin only needs to override
 * what it actually contributes.
 */
interface MeetingRoomUiPlugin {

    /**
     * Composable rendered as an overlay on top of the video grid.
     * Examples: emoji reactions floating animation, captions text.
     */
    @Composable
    fun OverlayContent(call: CallFacade, modifier: Modifier = Modifier) {
    }

    /**
     * Composable rendered inside the adaptive side panel (extra pane).
     * Only the first plugin that returns [hasPanelContent] == true is shown.
     */
    @Composable
    fun PanelContent(call: CallFacade, onClose: () -> Unit) {
    }

    /**
     * Whether this plugin provides side-panel content.
     * Used by the screen to decide whether to show the panel toggle.
     */
    val hasPanelContent: Boolean get() = false

    /**
     * Bottom-bar actions contributed by this plugin.
     * Each call to this function may observe state flows to reflect live status
     * (e.g., recording in progress, captions enabled).
     *
     * @param actions Root [MeetingRoomActions] for triggering operations.
     * @param isPanelOpen Whether the side panel is currently open.
     * @param onTogglePanel Callback to open/close the side panel.
     */
    @Composable
    fun bottomBarActions(
        actions: MeetingRoomActions,
        isPanelOpen: Boolean,
        onTogglePanel: () -> Unit,
    ): ImmutableList<BottomBarAction> = persistentListOf()

    /**
     * Content shown inside the "More actions" bottom sheet above the overflow grid.
     * Example: emoji picker.
     */
    @Composable
    fun MoreActionsSheetContent(actions: MeetingRoomActions) {
    }

    /**
     * Composable placed before the room-name title in the top bar.
     * Example: red recording dot or spinner during archiving.
     */
    @Composable
    fun TopBarTitleDecoration() {
    }

    /**
     * Icon buttons injected into the top-bar actions row.
     * Example: settings gear icon.
     */
    @Composable
    fun TopBarActions(actions: MeetingRoomActions) {
    }

    /**
     * Overlay rendered on the publisher's video card.
     * Example: blur level indicator + toggle button from the video-effects feature.
     *
     * @param participant  The local publisher participant.
     * @param isCameraEnabled Whether the local camera is currently active.
     */
    @Composable
    fun PublisherVideoOverlay(
        participant: PublisherParticipant,
        isCameraEnabled: Boolean,
        modifier: Modifier = Modifier,
    ) {
    }
}
