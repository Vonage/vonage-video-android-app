package com.vonage.android.kotlin.model

data class PublisherConfig(
    val name: String,
    val publishVideo: Boolean,
    val publishAudio: Boolean,
    val blurLevel: BlurLevel,
    val cameraIndex: Int,
)
