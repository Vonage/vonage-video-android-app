package com.vonage.android.screen.room.elements

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_CAMERA_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_CHAT_BADGE
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_CHAT_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_END_CALL_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_MIC_BUTTON
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_PARTICIPANTS_BADGE
import com.vonage.android.screen.room.components.BottomBarTestTags.BOTTOM_BAR_PARTICIPANTS_BUTTON
import com.vonage.android.util.ComposeTestElement

fun SemanticsNodeInteractionsProvider.bottomBar(
    testTag: String,
): BottomBarElement = BottomBarElement(this, testTag)

class BottomBarElement(
    nodeInteractionsProvider: SemanticsNodeInteractionsProvider,
    testTag: String,
) : ComposeTestElement(nodeInteractionsProvider, testTag) {

    val participantsButton: SemanticsNodeInteraction
        get() = child(BOTTOM_BAR_PARTICIPANTS_BUTTON)
    val participantsBadge: SemanticsNodeInteraction
        get() = child(BOTTOM_BAR_PARTICIPANTS_BADGE)
    val endCallButton: SemanticsNodeInteraction
        get() = child(BOTTOM_BAR_END_CALL_BUTTON)
    val chatButton: SemanticsNodeInteraction
        get() = child(BOTTOM_BAR_CHAT_BUTTON)
    val chatUnreadBadge: SemanticsNodeInteraction
        get() = child(BOTTOM_BAR_CHAT_BADGE)
    val cameraButton: SemanticsNodeInteraction
        get() = child(BOTTOM_BAR_CAMERA_BUTTON)
    val micButton: SemanticsNodeInteraction
        get() = child(BOTTOM_BAR_MIC_BUTTON)

    fun assertIsDisplayed(): BottomBarElement {
        micButton.assertIsDisplayed()
        cameraButton.assertIsDisplayed()
        endCallButton.assertIsDisplayed()
        return this
    }

    fun assertIsDisplayedWithParticipantBadge(expectedParticipantsCount: String): BottomBarElement {
        participantsButton.assertIsDisplayed()
        participantsBadge.assertIsDisplayed().assertTextEquals(expectedParticipantsCount)
        return this
    }

    fun assertIsDisplayedWithUnreadBadge(expectedUnreadCount: String): BottomBarElement {
        chatButton.assertIsDisplayed()
        chatUnreadBadge.assertIsDisplayed().assertTextEquals(expectedUnreadCount)
        return this
    }
}
