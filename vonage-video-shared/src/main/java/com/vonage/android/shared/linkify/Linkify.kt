package com.vonage.android.shared.linkify

object Linkify {

    fun extract(text: String): List<LinkSpec> =
        (extractEmails(text) + extractUrls(text)).pruneOverlaps()

    private fun extractUrls(text: String): List<LinkSpec> = extract(text, WebUrl)

    private fun extractEmails(text: String): List<LinkSpec> = extract(text, Email)

    private fun extract(
        text: String,
        spec: LinkifySpec,
    ): List<LinkSpec> {
        val matcher = spec.pattern.matcher(text)
        var matchStart: Int
        var matchEnd: Int
        val links = arrayListOf<LinkSpec>()

        while (matcher.find()) {
            matchStart = matcher.start(1)
            matchEnd = matcher.end()
            val url = spec.builder(text, matchStart, matchEnd)
            links.add(LinkSpec(spec.tag, url, matchStart, matchEnd))
        }
        return links
    }

    // from https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/text/util/Linkify.java;l=737;drc=4f9480b13d3cab52255608ac5913922ca4269ac5
    private fun List<LinkSpec>.pruneOverlaps(): List<LinkSpec> {
        val sortedList = sortedWith { a, b ->
            if (a.start < b.start) {
                return@sortedWith -1
            }
            if (a.start > b.start) {
                return@sortedWith 1
            }
            if (a.end < b.end) {
                return@sortedWith 1
            }
            if (a.end > b.end) {
                return@sortedWith -1
            }
            return@sortedWith 0
        }.toMutableList()

        var len: Int = sortedList.size
        var i = 0

        while (i < len - 1) {
            val a = sortedList[i]
            val b = sortedList[i + 1]
            var remove = -1
            if (a.start <= b.start && a.end > b.start) {
                if (b.end <= a.end) {
                    remove = i + 1
                } else if (a.end - a.start > b.end - b.start) {
                    remove = i + 1
                } else if (a.end - a.start < b.end - b.start) {
                    remove = i
                }
                if (remove != -1) {
                    sortedList.removeAt(remove)
                    len--
                    continue
                }
            }
            i++
        }
        return sortedList
    }
}
