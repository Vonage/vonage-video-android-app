package com.vonage.android.screen.room

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_BOTTOM_BAR
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_CONTENT
import com.vonage.android.screen.room.MeetingRoomScreenTestTags.MEETING_ROOM_TOP_BAR
import com.vonage.android.screen.room.elements.bottomBar
import com.vonage.android.screen.room.elements.meetingRoomContent
import com.vonage.android.screen.room.elements.topBar

class MeetingRoomScreenObject(
    compose: SemanticsNodeInteractionsProvider
) {
    val topBar = compose.topBar(testTag = MEETING_ROOM_TOP_BAR)
    val content = compose.meetingRoomContent(testTag = MEETING_ROOM_CONTENT)
    val bottomBar = compose.bottomBar(testTag = MEETING_ROOM_BOTTOM_BAR)
}
