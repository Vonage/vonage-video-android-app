package com.vonage.android.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.shared.ChatMessage
import kotlinx.collections.immutable.ImmutableList

@Suppress("UnusedParameter", "EmptyFunctionBlock")
@Composable
fun ChatPanel(
    title: String,
    sendLabel: String,
    jumpToBottomLabel: String,
    messages: ImmutableList<ChatMessage>,
    onCloseChat: () -> Unit,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
}
