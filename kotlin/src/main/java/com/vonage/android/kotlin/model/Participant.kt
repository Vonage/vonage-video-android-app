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

@Stable
interface PublisherParticipant : Participant {
    override val isPublisher: Boolean
        get() = true
    val camera: StateFlow<CameraType>
    val blurLevel: StateFlow<BlurLevel>
    fun toggleVideo()
    fun toggleAudio()
    fun cycleCamera()
    fun cycleCameraBlur()
    fun clean()
}

enum class CameraType(val index: Int) {
    UNKNOWN(-1),
    BACK(0),
    FRONT(1);

    companion object {
        private val map = entries.toTypedArray().associateBy(CameraType::index)
        infix fun fromInt(index: Int): CameraType? = map[index]
    }
}

enum class VideoSource {
    CAMERA,
    SCREEN
}
