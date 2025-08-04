package com.vonage.android.kotlin.model

import android.view.View
import kotlinx.coroutines.flow.Flow

interface Participant {
    val id: String
    val name: String
    val isMicEnabled: Boolean
    val isCameraEnabled: Boolean
    val view: View
    val isSpeaking: Boolean
}

data class VeraPublisher(
    override val id: String,
    override val name: String,
    override val isMicEnabled: Boolean,
    override val isCameraEnabled: Boolean,
    override val view: View,
    override val isSpeaking: Boolean,
    val audioLevel: Flow<Float>,
    val cameraIndex: Int,
    val cycleCamera: () -> Unit,
    val blurLevel: BlurLevel,
    val setCameraBlur: (BlurLevel) -> Unit,
) : Participant

data class VeraSubscriber(
    override val id: String,
    override val name: String,
    override val isMicEnabled: Boolean,
    override val isCameraEnabled: Boolean,
    override val view: View,
    override val isSpeaking: Boolean,
) : Participant
