package com.vonage.android.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.components.VonageTextField
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.Close
import com.vonage.android.compose.vivid.icons.solid.MessageSent
import com.vonage.android.shared.ChatMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

val dateFormat = SimpleDateFormat("hh:mm a", Locale.current.platformLocale)

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
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        scope.launch {
            listState.scrollToItem(0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VonageVideoTheme.colors.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text = title,
                color = VonageVideoTheme.colors.onSurface,
                style = VonageVideoTheme.typography.bodyBase,
            )
            IconButton(
                onClick = onCloseChat,
            ) {
                Icon(
                    imageVector = VividIcons.Line.Close,
                    tint = VonageVideoTheme.colors.onSurface,
                    contentDescription = null,
                )
            }
        }
        ChatPanelMessages(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            jumpToBottomLabel = jumpToBottomLabel,
            scrollState = listState,
            messages = messages,
        )
        ChatPanelInput(
            sendLabel = sendLabel,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp),
            onSendMessage = onSendMessage,
        )
    }
}

@Composable
private fun ChatPanelMessages(
    jumpToBottomLabel: String,
    scrollState: LazyListState,
    messages: ImmutableList<ChatMessage>,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier,
    ) {
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            modifier = Modifier
                .fillMaxSize(),
        ) {
            items(
                items = messages,
                key = { chatSignal -> chatSignal.id },
            ) { chatMessage ->
                ChatRow(
                    userName = chatMessage.participantName,
                    message = chatMessage.text,
                    date = chatMessage.date,
                    dateFormat = dateFormat,
                )
            }
        }
        val jumpThreshold = with(LocalDensity.current) {
            56.dp.toPx()
        }

        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToBottom(
            label = jumpToBottomLabel,
            enabled = jumpToBottomButtonEnabled,
            onClick = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun ChatPanelInput(
    sendLabel: String,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var chatInputValue by remember { mutableStateOf("") }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VonageTextField(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            value = chatInputValue,
            onValueChange = {
                chatInputValue = it
            },
            singleLine = false,
            maxLines = 3,
            placeholder = {
                Text(
                    text = sendLabel,
                    color = VonageVideoTheme.colors.primary,
                )
            },
        )
        IconButton(
            onClick = {
                onSendMessage(chatInputValue)
                chatInputValue = ""
            },
            enabled = chatInputValue.isNotBlank(),
        ) {
            Icon(
                imageVector = VividIcons.Solid.MessageSent,
                tint = VonageVideoTheme.colors.onSurface,
                contentDescription = null,
            )
        }
    }
}

@PreviewLightDark
@Composable
internal fun ChatPanelPreview() {
    VonageVideoTheme {
        ChatPanel(
            title = "Chat Preview",
            sendLabel = "Send message preview",
            jumpToBottomLabel = "Jump to bottom preview",
            messages = persistentListOf(),
            onSendMessage = {},
            onCloseChat = {},
        )
    }
}
