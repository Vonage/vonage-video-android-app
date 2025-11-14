package com.vonage.android.kotlin.model

import android.view.View
import androidx.compose.runtime.Stable
import com.opentok.android.Session
import kotlinx.coroutines.flow.StateFlow

@Stable
interface Participant {
    val id: String
    val isPublisher: Boolean
    val creationTime: Long
    val videoSource: VideoSource
    val name: String
    val isMicEnabled: StateFlow<Boolean>
    val isCameraEnabled: StateFlow<Boolean>
    val isTalking: StateFlow<Boolean>
    val audioLevel: StateFlow<Float>
    val view: View
    fun changeVisibility(visible: Boolean) {}
    fun clean(session: Session) {}
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
    override val view: View,
    override val isTalking: StateFlow<Boolean>,
    override val audioLevel: StateFlow<Float>,
    override val creationTime: Long,
    val cameraIndex: Int,
    val cycleCamera: () -> Unit,
    val blurLevel: BlurLevel,
    val setCameraBlur: (BlurLevel) -> Unit,
    val toggleMic: () -> Boolean,
    val toggleCamera: () -> Boolean,
) : Participant {
    override val isPublisher: Boolean = true
}

data class VeraScreenPublisher(
    override val id: String,
    override val videoSource: VideoSource,
    override val name: String,
    override val isMicEnabled: StateFlow<Boolean>,
    override val isCameraEnabled: StateFlow<Boolean>,
    override val view: View,
    override val isTalking: StateFlow<Boolean>,
    override val audioLevel: StateFlow<Float>,
    override val creationTime: Long,
) : Participant {
    override val isPublisher: Boolean = true
}
