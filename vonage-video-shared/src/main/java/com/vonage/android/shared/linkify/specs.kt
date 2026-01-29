@file:SuppressLint("RestrictedApi")

package com.vonage.android.shared.linkify

import android.annotation.SuppressLint
import androidx.core.util.PatternsCompat.AUTOLINK_EMAIL_ADDRESS
import androidx.core.util.PatternsCompat.AUTOLINK_WEB_URL
import java.util.regex.Pattern

internal sealed class LinkifySpec(
    val pattern: Pattern,
    val tag: LinkTag,
    val builder: (String, Int, Int) -> String,
)

internal data object Email : LinkifySpec(
    pattern = AUTOLINK_EMAIL_ADDRESS,
    tag = LinkTag.EMAIL,
    builder = { text, start, end ->
        "mailto:${text.substring(start, end)}"
    }
)

internal data object WebUrl : LinkifySpec(
    pattern = AUTOLINK_WEB_URL,
    tag = LinkTag.URL,
    builder = { text, start, end ->
        text.substring(start, end).prependHttpsIfNeeded()
    }
)

private fun String.prependHttpsIfNeeded() =
    if (!startsWith("http://") && !startsWith("https://")) {
        "https://$this"
    } else this
