package com.vonage.android.util

const val MAX_ROOM_NAME_LENGTH = 60

fun String.isValidRoomName(): Boolean =
    Regex("^[a-z0-9_+\\-]+$").matches(this)
