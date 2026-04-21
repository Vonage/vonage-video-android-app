package com.vonage.android.plugins

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.R
import com.vonage.android.chat.ui.ChatPanel
import com.vonage.android.chat.ui.chatAction
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.ChatState
import com.vonage.android.meetingroom.MeetingRoomActions
import com.vonage.android.meetingroom.MeetingRoomUiPlugin
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow

/**
 * [MeetingRoomUiPlugin] for the chat feature.
 *
 * Provides:
 * - Side panel content (chat messages + input).
 * - Bottom-bar action to open/close chat with unread badge.
 *
 * @param chatStateFlow  Live chat state flow from the active [CallFacade].
 *                       Updated by the ViewModel when a session connects.
 */
class ChatUiPlugin(
    private val chatStateFlow: StateFlow<ChatState?>,
) : MeetingRoomUiPlugin {

    override val hasPanelContent: Boolean = true

    @Composable
    override fun PanelContent(call: CallFacade, onClose: () -> Unit) {
        val chatState by chatStateFlow.collectAsStateWithLifecycle()
        ChatPanel(
            title = stringResource(R.string.chat_panel_title),
            sendLabel = stringResource(R.string.chat_panel_input_text_placeholder),
            jumpToBottomLabel = stringResource(R.string.chat_panel_jump_to_bottom),
            messages = chatState?.messages?.toImmutableList() ?: persistentListOf(),
            onSendMessage = { message -> call.sendChatMessage(message) },
            onCloseChat = onClose,
        )
    }

    @Composable
    override fun bottomBarActions(
        actions: MeetingRoomActions,
        isPanelOpen: Boolean,
        onTogglePanel: () -> Unit,
    ): ImmutableList<BottomBarAction> {
        val chatState by chatStateFlow.collectAsStateWithLifecycle()
        return listOfNotNull(
            chatAction(
                label = stringResource(R.string.chat),
                isSelected = isPanelOpen,
                badgeCount = chatState?.unreadCount ?: 0,
                onShowChat = onTogglePanel,
            )
        ).toImmutableList()
    }
}
