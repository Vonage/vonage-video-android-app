package com.vonage.android.util

fun String.getInitials(): String =
    trim()
        .splitName()
        .filterNotNull()
        .filter { it.isNotBlank() }
        .joinToString(
            separator = "",
            truncated = "",
            limit = 2,
        ) { s ->
            s.first().uppercase()
        }

private fun CharSequence.splitName(): List<String?> {
    val names = trim().split(Regex("\\s+"))
    return when (names.size) {
        1 -> listOf(names.firstOrNull())
        in (2..10) -> listOf(names.firstOrNull(), names.lastOrNull())
        else -> listOf(null)
    }
}
