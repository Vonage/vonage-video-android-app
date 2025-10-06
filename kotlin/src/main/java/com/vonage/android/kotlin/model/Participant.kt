package com.vonage.android.kotlin.model

import android.view.View
import kotlinx.coroutines.flow.MutableStateFlow

interface Participant {
    val id: String
    val videoSource: VideoSource
    val name: String
    val isMicEnabled: MutableStateFlow<Boolean>
    val isCameraEnabled: MutableStateFlow<Boolean>
    val isSpeaking: MutableStateFlow<Boolean>
    val view: View
}

enum class VideoSource {
    CAMERA,
    SCREEN
}

data class VeraPublisher(
    override val id: String,
    override val videoSource: VideoSource,
    override val name: String,
    override val isMicEnabled: MutableStateFlow<Boolean>,
    override val isCameraEnabled: MutableStateFlow<Boolean>,
    override val view: View,
    override val isSpeaking: MutableStateFlow<Boolean>,
    val cameraIndex: Int,
    val cycleCamera: () -> Unit,
    val blurLevel: BlurLevel,
    val setCameraBlur: (BlurLevel) -> Unit,
) : Participant

data class VeraScreenPublisher(
    override val id: String,
    override val videoSource: VideoSource,
    override val name: String,
    override val isMicEnabled: MutableStateFlow<Boolean>,
    override val isCameraEnabled: MutableStateFlow<Boolean>,
    override val view: View,
    override val isSpeaking: MutableStateFlow<Boolean>,
) : Participant

data class VeraSubscriber(
    override val id: String,
    override val videoSource: VideoSource,
    override val name: String,
    override val isMicEnabled: MutableStateFlow<Boolean>,
    override val isCameraEnabled: MutableStateFlow<Boolean>,
    override val isSpeaking: MutableStateFlow<Boolean>,
    override val view: View,
) : Participant
