package com.vonage.android.settings.util

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
internal fun Long.formatBytes(): String = when {
    this >= 1_000_000 -> String.format("%.1f MB", this / 1_000_000.0)
    this >= 1_000 -> String.format("%.1f KB", this / 1_000.0)
    else -> "$this B"
}

@SuppressLint("DefaultLocale")
internal fun Long.formatBitrate(): String = when {
    this >= 1_000_000 -> String.format("%.1f Mbps", this / 1_000_000.0)
    this >= 1_000 -> String.format("%.1f Kbps", this / 1_000.0)
    else -> "$this bps"
}
