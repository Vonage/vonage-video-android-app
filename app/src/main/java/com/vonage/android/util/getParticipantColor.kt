package com.vonage.android.util

import androidx.compose.ui.graphics.Color

fun String.getParticipantColor(): Color {
    val colorMap = listOf(
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

    val asciiSum = this.sumOf { it.code }
    return colorMap[asciiSum % 10]
}
