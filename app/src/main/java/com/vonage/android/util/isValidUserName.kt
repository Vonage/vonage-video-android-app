package com.vonage.android.util

const val MAX_USER_NAME_LENGTH = 60

fun String.isValidUserName(): Boolean =
    trim().let { length in 1..MAX_USER_NAME_LENGTH }

fun String.sanitizeUserName(): String =
    trim().replace("\\s{2,}".toRegex(), " ")
