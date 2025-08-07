package com.vonage.android.screen.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.components.VonageTextField
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.ChatMessage
import com.vonage.android.screen.components.ControlButton
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun ChatPanel(
    messages: ImmutableList<ChatMessage>,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        scope.launch {
            listState.scrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VonageVideoTheme.colors.surface),
    ) {
        ChatPanelMessages(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            scrollState = listState,
            messages = messages,
        )
        ChatPanelInput(
            modifier = Modifier
                .navigationBarsPadding(),
            onMessageSent = {

            }
        )
    }
}

@Composable
private fun ChatPanelMessages(
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
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
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
    modifier: Modifier = Modifier,
    onMessageSent: (String) -> Unit,
) {
    var chatInputValue by remember { mutableStateOf("") }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VonageTextField(
            modifier = Modifier,
            value = chatInputValue,
            onValueChange = {
                chatInputValue = it
            },
        )
        ControlButton(
            modifier = Modifier,
            onClick = {
                onMessageSent(chatInputValue)
            },
            icon = Icons.AutoMirrored.Default.Send,
            isActive = true,
        )
    }
}

@PreviewLightDark
@Preview
@Composable
internal fun ChatPanelPreview() {
    VonageVideoTheme {
        ChatPanel(
            messages = buildChatMessages(20).reversed().toImmutableList(),
        )
    }
}

@Composable
fun buildChatMessages(count: Int): List<ChatMessage> {
    val messages = mutableListOf<ChatMessage>()
    for (i in 1..count) {
        messages += ChatMessage(
            id = "id-$i",
            date = Date(),
            participantName = "name $i",
            text = "message $i"
        )
    }
    return messages
}
