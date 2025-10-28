package com.vonage.android.elements

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import com.vonage.android.compose.components.AvatarInitialsTestTags.USER_INITIALS_ICON_TAG
import com.vonage.android.compose.components.AvatarInitialsTestTags.USER_INITIALS_TEXT_TAG
import com.vonage.android.util.ComposeTestElement

fun SemanticsNodeInteractionsProvider.avatarInitials(
    testTag: String,
): AvatarInitialsElement = AvatarInitialsElement(this, testTag)

class AvatarInitialsElement(
    nodeInteractionsProvider: SemanticsNodeInteractionsProvider,
    testTag: String,
) : ComposeTestElement(nodeInteractionsProvider, testTag) {

    val text: SemanticsNodeInteraction
        get() = child(USER_INITIALS_TEXT_TAG)
    val icon: SemanticsNodeInteraction
        get() = child(USER_INITIALS_ICON_TAG)

    fun assertIsDisplayedWithText(expectedText: String) {
        text.assertIsDisplayed().assertTextEquals(expectedText)
        icon.assertIsNotDisplayed()
    }

    fun assertIsDisplayedWithIcon() {
        text.assertIsNotDisplayed()
        icon.assertIsDisplayed()
    }
}
