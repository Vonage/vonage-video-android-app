package com.vonage.android.screen.room.elements

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_SHARE_ACTION
import com.vonage.android.screen.room.components.TopBarTestTags.TOP_BAR_TITLE
import com.vonage.android.util.ComposeTestElement

fun SemanticsNodeInteractionsProvider.topBar(
    testTag: String,
): TopBarElement = TopBarElement(this, testTag)

class TopBarElement(
    nodeInteractionsProvider: SemanticsNodeInteractionsProvider,
    testTag: String,
) : ComposeTestElement(nodeInteractionsProvider, testTag) {

    val title: SemanticsNodeInteraction
        get() = child(TOP_BAR_TITLE)
    val shareIcon: SemanticsNodeInteraction
        get() = child(TOP_BAR_SHARE_ACTION)

    fun assertIsDisplayedWithTitle(expectedText: String) {
        title.assertIsDisplayed().assertTextEquals(expectedText)
        shareIcon.assertIsDisplayed()
    }
}
