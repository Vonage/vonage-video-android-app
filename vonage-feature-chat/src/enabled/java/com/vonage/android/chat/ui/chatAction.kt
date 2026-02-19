package com.vonage.android.chat.ui

import androidx.compose.runtime.Composable
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.BottomBarActionType
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Chat2

@Suppress("UnusedParameter", "FunctionOnlyReturningConstant")
@Composable
fun chatAction(
    onShowChat: () -> Unit,
    label: String,
    isSelected: Boolean,
    badgeCount: Int,
): BottomBarAction? = BottomBarAction(
    type = BottomBarActionType.CHAT,
    icon = VividIcons.Solid.Chat2,
    label = label,
    isSelected = isSelected,
    badgeCount = badgeCount,
    onClick = onShowChat,
)
