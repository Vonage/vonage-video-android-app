package com.vonage.android.plugins

import androidx.compose.runtime.Composable
import com.vonage.android.meetingroom.MeetingRoomActions
import com.vonage.android.meetingroom.MeetingRoomUiPlugin
import com.vonage.android.settings.ui.SettingsIcon

/**
 * [MeetingRoomUiPlugin] for the settings feature.
 *
 * Provides:
 * - Settings gear icon in the top-bar action row.
 */
class SettingsUiPlugin : MeetingRoomUiPlugin {

    @Composable
    override fun TopBarActions(actions: MeetingRoomActions) {
        SettingsIcon(navigateToSettings = actions.onSettings)
    }
}
