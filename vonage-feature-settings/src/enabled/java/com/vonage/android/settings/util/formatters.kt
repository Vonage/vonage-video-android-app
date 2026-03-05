package com.vonage.android.settings.util

import android.annotation.SuppressLint
import java.util.Locale

@SuppressLint("DefaultLocale", "MagicNumber")
internal fun Long.formatBytes(): String = when {
    this >= ONE_MILLION -> String.format(Locale.getDefault(), "%.1f MB", this / ONE_MILLION.toFloat())
    this >= ONE_THOUSAND -> String.format(Locale.getDefault(), "%.1f KB", this / ONE_THOUSAND.toFloat())
    else -> "$this B"
}

@SuppressLint("DefaultLocale", "MagicNumber")
internal fun Long.formatBitrate(): String = when {
    this >= ONE_MILLION -> String.format(Locale.getDefault(), "%.1f Mbps", this / ONE_MILLION.toFloat())
    this >= ONE_THOUSAND -> String.format(Locale.getDefault(), "%.1f Kbps", this / ONE_THOUSAND.toFloat())
    else -> "$this bps"
}

private const val ONE_MILLION = 1_000_000
private const val ONE_THOUSAND = 1_000
