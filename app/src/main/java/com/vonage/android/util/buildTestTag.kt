package com.vonage.android.util

internal fun String.buildTestTag(enabled: Boolean): String {
    val suffix = if (enabled) "on" else "off"
    return "$this-$suffix"
}
