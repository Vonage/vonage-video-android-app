package com.vonage.android.compose.preview

import androidx.compose.runtime.Composable
import com.vonage.android.shared.ChatMessage
import java.util.Date
import java.util.UUID

@Composable
fun buildChatMessages(count: Int): List<ChatMessage> {
    val messages = mutableListOf<ChatMessage>()
    for (i in 1..count) {
        messages += ChatMessage(
            id = UUID.randomUUID(),
            date = Date(),
            participantName = "name $i",
            text = "message $i"
        )
    }
    return messages
}
