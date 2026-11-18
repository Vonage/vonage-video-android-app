package com.vonage.android.compose.util

fun String.buildTestTag(enabled: Boolean): String {
    val suffix = if (enabled) "enabled" else "disabled"
    return "$this-$suffix"
}
