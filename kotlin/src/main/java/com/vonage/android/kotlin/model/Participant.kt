package com.vonage.android.kotlin.model

import android.view.View

interface Participant {
    val id: String
    val videoSource: VideoSource
    val name: String
    val isMicEnabled: Boolean
    val isCameraEnabled: Boolean
    val view: View
    val isSpeaking: Boolean
}

enum class VideoSource {
    CAMERA,
    SCREEN
}

data class VeraPublisher(
    override val id: String,
    override val videoSource: VideoSource,
    override val name: String,
    override val isMicEnabled: Boolean,
    override val isCameraEnabled: Boolean,
    override val view: View,
    override val isSpeaking: Boolean,
    val cameraIndex: Int,
    val cycleCamera: () -> Unit,
    val blurLevel: BlurLevel,
    val setCameraBlur: (BlurLevel) -> Unit,
) : Participant

data class VeraScreenPublisher(
    override val id: String,
    override val videoSource: VideoSource,
    override val name: String,
    override val isMicEnabled: Boolean,
    override val isCameraEnabled: Boolean,
    override val view: View,
    override val isSpeaking: Boolean,
) : Participant

data class VeraSubscriber(
    override val id: String,
    override val videoSource: VideoSource,
    override val name: String,
    override val isMicEnabled: Boolean,
    override val isCameraEnabled: Boolean,
    override val view: View,
    override val isSpeaking: Boolean,
) : Participant
