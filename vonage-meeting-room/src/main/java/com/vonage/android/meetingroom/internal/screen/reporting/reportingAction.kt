package com.vonage.android.meetingroom.internal.screen.reporting

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.compose.components.bottombar.BottomBarActionType
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Warning
import com.vonage.android.meetingroom.R

@Composable
internal fun reportingAction(
    onClick: () -> Unit,
): BottomBarAction? =
    BottomBarAction(
        type = BottomBarActionType.REPORT,
        icon = VividIcons.Solid.Warning,
        label = stringResource(R.string.report_bottombar_button_label),
        isSelected = false,
        onClick = onClick,
    )
