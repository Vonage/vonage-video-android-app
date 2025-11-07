package com.vonage.android.kotlin.model

import android.view.View
import kotlinx.coroutines.flow.StateFlow

interface Participant {
    val id: String
    val videoSource: VideoSource
    val name: String
    val isMicEnabled: StateFlow<Boolean>
    val isCameraEnabled: StateFlow<Boolean>
    val isSpeaking: StateFlow<Boolean>
    val view: StateFlow<View>
}

enum class VideoSource {
    CAMERA,
    SCREEN
}

data class VeraPublisher(
    override val id: String,
    override val videoSource: VideoSource,
    override val name: String,
    override val isMicEnabled: StateFlow<Boolean>,
    override val isCameraEnabled: StateFlow<Boolean>,
    override val view: StateFlow<View>,
    override val isSpeaking: StateFlow<Boolean>,
    val cameraIndex: Int,
    val cycleCamera: () -> Unit,
    val blurLevel: BlurLevel,
    val setCameraBlur: (BlurLevel) -> Unit,
    val toggleMic: () -> Boolean,
    val toggleCamera: () -> Boolean,
) : Participant

data class VeraScreenPublisher(
    override val id: String,
    override val videoSource: VideoSource,
    override val name: String,
    override val isMicEnabled: StateFlow<Boolean>,
    override val isCameraEnabled: StateFlow<Boolean>,
    override val view: StateFlow<View>,
    override val isSpeaking: StateFlow<Boolean>,
) : Participant
