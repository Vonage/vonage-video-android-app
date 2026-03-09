package com.vonage.android.shared

/**
 * Represents a displayed emoji reaction from a participant.
 *
 * @property id Unique identifier for the reaction (timestamp)
 * @property emoji The emoji character
 * @property sender Display name of the sender
 * @property isYou True if sent by the local user
 * @property startTime Timestamp when the reaction was created
 */
data class EmojiReaction(
    val id: Long,
    val emoji: String,
    val sender: String,
    val isYou: Boolean,
    val startTime: Long,
)
