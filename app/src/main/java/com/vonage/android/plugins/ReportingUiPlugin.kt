package com.vonage.android.plugins

import androidx.compose.runtime.Composable
import com.vonage.android.compose.components.bottombar.BottomBarAction
import com.vonage.android.meetingroom.MeetingRoomActions
import com.vonage.android.meetingroom.MeetingRoomUiPlugin
import com.vonage.android.screen.reporting.ReportIssueScreen
import com.vonage.android.screen.reporting.components.reportingAction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * [MeetingRoomUiPlugin] for the issue reporting feature.
 *
 * Provides:
 * - "Report Issue" action in the overflow bottom-bar actions grid.
 * - [ReportIssueScreen] displayed inside the more-actions bottom sheet.
 */
class ReportingUiPlugin : MeetingRoomUiPlugin {

    @Composable
    override fun bottomBarActions(
        actions: MeetingRoomActions,
        isPanelOpen: Boolean,
        onTogglePanel: () -> Unit,
    ): ImmutableList<BottomBarAction> = persistentListOf(reportingAction(onClick = {}))

    @Composable
    override fun MoreActionsSheetContent(actions: MeetingRoomActions) {
        ReportIssueScreen(onClose = {})
    }
}
