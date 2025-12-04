package com.vonage.android.screen.room.components.bottombar

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomBarAction(
    val type: BottomBarActionType,
    val icon: ImageVector,
    val label: String,
    val isSelected: Boolean = false,
    val badgeCount: Int = 0,
    val onClick: () -> Unit,
)

enum class BottomBarActionType {
    CHANGE_LAYOUT,
    CHAT,
    PARTICIPANTS,
    RECORD_SESSION,
    SCREEN_SHARING,
    CAPTIONS,
    REPORT,
}
