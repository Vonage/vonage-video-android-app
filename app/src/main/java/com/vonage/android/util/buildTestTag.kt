package com.vonage.android.util

internal fun String.buildTestTag(enabled: Boolean): String {
    val suffix = if (enabled) "enabled" else "disabled"
    return "$this-$suffix"
}
