package com.vonage.android.util

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag

open class ComposeTestElement(
    val nodeInteractionsProvider: SemanticsNodeInteractionsProvider,
    val testTag: String,
) {
    val element: SemanticsNodeInteraction
        get() = nodeInteractionsProvider.onNodeWithTag(testTag)

    fun child(tag: String) = element.onChildren().filter(hasTestTag(tag)).onFirst()
}
