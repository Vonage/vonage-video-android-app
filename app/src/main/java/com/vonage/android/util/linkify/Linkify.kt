package com.vonage.android.util.linkify

import android.annotation.SuppressLint
import androidx.core.util.PatternsCompat

// extract patterns to get rid of this SuppressLint
@SuppressLint("RestrictedApi")
object Linkify {

    fun extract(text: String): List<LinkInfos> =
        extractEmails(text) + extractUrls(text)

    private fun extractUrls(text: String): List<LinkInfos> {
        val p = PatternsCompat.AUTOLINK_WEB_URL
        val matcher = p.matcher(text)
        var matchStart: Int
        var matchEnd: Int
        val links = arrayListOf<LinkInfos>()

        while (matcher.find()) {
            matchStart = matcher.start(1)
            matchEnd = matcher.end()

            var url = text.substring(matchStart, matchEnd)
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "https://$url"

            links.add(LinkInfos(LinkInfoTag.URL, url, matchStart, matchEnd))
        }
        return links
    }

    private fun extractEmails(text: String): List<LinkInfos> {
        val p = PatternsCompat.AUTOLINK_EMAIL_ADDRESS
        val matcher = p.matcher(text)
        var matchStart: Int
        var matchEnd: Int
        val links = arrayListOf<LinkInfos>()

        while (matcher.find()) {
            matchStart = matcher.start(1)
            matchEnd = matcher.end()

            var url = text.substring(matchStart, matchEnd)
            url = "mailto:$url"

            links.add(LinkInfos(LinkInfoTag.EMAIL, url, matchStart, matchEnd))
        }
        return links
    }

    enum class LinkInfoTag {
        URL,
        EMAIL
    }

    data class LinkInfos(
        val tag: LinkInfoTag,
        val url: String,
        val start: Int,
        val end: Int
    )
}
