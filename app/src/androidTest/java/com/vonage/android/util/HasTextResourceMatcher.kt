package com.vonage.android.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText

fun hasText(
    context: Context,
    @StringRes text: Int,
    substring: Boolean = false,
    ignoreCase: Boolean = false
): SemanticsMatcher =
    hasText(context.resources.getString(text), substring, ignoreCase)
