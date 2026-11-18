package com.vonage.android.compose.linkify

enum class LinkTag {
    URL,
    EMAIL
}

data class LinkSpec(
    val tag: LinkTag,
    val url: String,
    val start: Int,
    val end: Int
)
