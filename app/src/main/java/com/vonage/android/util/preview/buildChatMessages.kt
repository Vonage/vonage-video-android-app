package com.vonage.android.util.preview

import com.vonage.android.kotlin.model.ChatMessage
import java.util.Date
import java.util.UUID

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
