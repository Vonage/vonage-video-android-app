package com.vonage.android.chat.ui

import androidx.compose.runtime.Composable
import com.vonage.android.compose.components.bottombar.BottomBarAction

@Suppress("UnusedParameter", "FunctionOnlyReturningConstant")
@Composable
fun chatAction(
    onShowChat: () -> Unit,
    label: String,
    isSelected: Boolean,
    badgeCount: Int,
): BottomBarAction? = null
