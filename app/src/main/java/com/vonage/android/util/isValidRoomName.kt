package com.vonage.android.util

fun String.isValidRoomName(): Boolean =
    Regex("^[a-z0-9_+\\-]+$").matches(this)
