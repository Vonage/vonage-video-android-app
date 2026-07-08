package com.vonage.android.shared

fun String.buildTestTag(enabled: Boolean): String {
    val suffix = if (enabled) "enabled" else "disabled"
    return "$this-$suffix"
}
