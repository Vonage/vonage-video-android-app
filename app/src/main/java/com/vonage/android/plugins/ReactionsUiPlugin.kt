package com.vonage.android.plugins

import androidx.compose.runtime.Composable
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.meetingroom.MeetingRoomActions
import com.vonage.android.meetingroom.MeetingRoomUiPlugin
import com.vonage.android.reactions.ui.EmojiReactionOverlay
import com.vonage.android.reactions.ui.EmojiSelector

/**
 * [MeetingRoomUiPlugin] for the reactions/emoji feature.
 *
 * Provides:
 * - Floating emoji animations overlay on the video grid.
 * - Emoji picker inside the "More actions" bottom sheet.
 */
class ReactionsUiPlugin : MeetingRoomUiPlugin {

    @Composable
    override fun OverlayContent(call: CallFacade, modifier: androidx.compose.ui.Modifier) {
        EmojiReactionOverlay(call = call, modifier = modifier)
    }

    @Composable
    override fun MoreActionsSheetContent(actions: MeetingRoomActions) {
        EmojiSelector(onEmojiClick = { emoji -> actions.onEmojiSent(emoji) })
    }
}
