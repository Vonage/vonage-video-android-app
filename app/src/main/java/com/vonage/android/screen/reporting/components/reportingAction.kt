package com.vonage.android.screen.reporting.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vonage.android.R
import com.vonage.android.screen.room.components.ExtraAction

@Composable
fun reportingAction(
    onClick: () -> Unit,
): ExtraAction =
    ExtraAction(
        id = 4,
        icon = Icons.Default.BugReport,
        label = stringResource(R.string.report_bottombar_button_label),
        isSelected = false,
        onClick = onClick,
    )
