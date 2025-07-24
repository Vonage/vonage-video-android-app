package com.vonage.android.kotlin.model

data class PublisherConfig(
    val name: String,
    val publishVideo: Boolean,
    val publishAudio: Boolean,
    val blurLevel: BlurLevel = BlurLevel.LOW, // pending read this value after publisher recreation
    val cameraIndex: Int = 0, // pending read this value after publisher recreation
)
