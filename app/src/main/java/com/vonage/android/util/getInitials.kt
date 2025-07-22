package com.vonage.android.util

fun String.getInitials(limit: Int = 2): String =
    splitName()
        .filterNotNull()
        .joinToString(
            separator = "",
            truncated = "",
            limit = limit,
        ) { s ->
            s.uppercase()
        }

private fun CharSequence.splitName(): List<Char?> {
    val names = trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .map { it.first() }
        .filter { it.isLetterOrDigit() }
    return when (names.size) {
        1 -> listOf(names.firstOrNull())
        in (2..Int.MAX_VALUE) -> listOf(names.firstOrNull(), names.lastOrNull())
        else -> listOf(null)
    }
}
