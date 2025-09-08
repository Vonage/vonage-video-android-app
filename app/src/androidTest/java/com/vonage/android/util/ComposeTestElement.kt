package com.vonage.android.util

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag

open class ComposeTestElement(
    val nodeInteractionsProvider: SemanticsNodeInteractionsProvider,
    val testTag: String,
) {
    val element: SemanticsNodeInteraction
        get() = nodeInteractionsProvider.onNodeWithTag(testTag, useUnmergedTree = true)

    fun child(tag: String) = element.onChildren().filterToOne(hasTestTag(tag))

    fun element(tag: String) = nodeInteractionsProvider.onNodeWithTag(tag, useUnmergedTree = true)
}
