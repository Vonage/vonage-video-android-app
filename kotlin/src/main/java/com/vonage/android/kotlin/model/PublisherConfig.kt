package com.vonage.android.kotlin.model

data class PublisherConfig(
    var name: String,
    val publishVideo: Boolean,
    val publishAudio: Boolean,
)