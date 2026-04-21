package com.vonage.android.meetingroom

import androidx.compose.runtime.compositionLocalOf
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * CompositionLocal that provides the list of active [MeetingRoomUiPlugin]s to any
 * composable in the meeting room hierarchy without requiring parameter threading.
 */
val LocalMeetingRoomPlugins = compositionLocalOf<ImmutableList<MeetingRoomUiPlugin>> {
    persistentListOf()
}
