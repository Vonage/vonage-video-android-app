package com.vonage.android.screen.room.elements

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import com.vonage.android.screen.room.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_GRID
import com.vonage.android.util.ComposeTestElement

fun SemanticsNodeInteractionsProvider.meetingRoomContent(
    testTag: String,
): MeetingRoomElement = MeetingRoomElement(this, testTag)

class MeetingRoomElement(
    nodeInteractionsProvider: SemanticsNodeInteractionsProvider,
    testTag: String,
) : ComposeTestElement(nodeInteractionsProvider, testTag) {

    val grid: SemanticsNodeInteraction
        get() = child(MEETING_ROOM_PARTICIPANTS_GRID)

    fun assertIsDisplayed() {
        grid.assertIsDisplayed()
    }
}
