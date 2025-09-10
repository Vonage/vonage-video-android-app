package com.vonage.android.screen.reporting.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.runtime.Composable
import com.vonage.android.screen.room.components.ExtraAction

@Composable
fun reportingAction(
    onClick: () -> Unit,
): ExtraAction =
    ExtraAction(
        id = 4,
        icon = Icons.Default.BugReport,
        label = "Report issue",
        isSelected = false,
        onClick = onClick,
    )
