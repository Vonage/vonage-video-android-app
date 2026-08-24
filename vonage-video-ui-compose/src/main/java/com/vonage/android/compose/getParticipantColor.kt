package com.vonage.android.compose

import androidx.compose.ui.graphics.Color

@Suppress("MagicNumber")
private val participantColors = listOf(
    Color(0xFFf44336),
    Color(0xFF607d8b),
    Color(0xFF9c27b0),
    Color(0xFF673ab7),
    Color(0xFF3f51b5),
    Color(0xFF2196f3),
    Color(0xFFff5722),
    Color(0xFF00bcd4),
    Color(0xFFffc107),
    Color(0xFF4caf50),
)

fun String.getParticipantColor(): Color {
    val asciiSum = this.sumOf { it.code }
    return participantColors[asciiSum % participantColors.size]
}
