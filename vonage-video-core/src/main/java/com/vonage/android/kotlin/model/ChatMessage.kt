package com.vonage.android.kotlin.model

import java.util.Date
import java.util.UUID

data class ChatMessage(
    val id: UUID,
    val date: Date,
    val participantName: String,
    val text: String,
)
