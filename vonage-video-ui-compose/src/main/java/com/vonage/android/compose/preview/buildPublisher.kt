package com.vonage.android.compose.preview

import android.view.View
import androidx.compose.runtime.Composable
import com.vonage.android.kotlin.model.BlurLevel
import com.vonage.android.kotlin.model.CameraType
import com.vonage.android.kotlin.model.PublisherParticipant
import com.vonage.android.kotlin.model.VideoSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun buildPublisher() = object : PublisherParticipant {

    override val id: String = "preview-id"
    override val connectionId: String = "connection-id"
    override val isPublisher: Boolean = true
    override val creationTime: Long = 1L
    override val isScreenShare: Boolean = false
    override val videoSource: VideoSource = VideoSource.CAMERA
    override val camera: StateFlow<CameraType> = MutableStateFlow(CameraType.FRONT)
    override val blurLevel: StateFlow<BlurLevel> = MutableStateFlow(BlurLevel.NONE)
    override val name: String = "Preview publisher"
    override val isMicEnabled: StateFlow<Boolean> = MutableStateFlow(true)
    override val isCameraEnabled: StateFlow<Boolean> = MutableStateFlow(true)
    override val isTalking: StateFlow<Boolean> = MutableStateFlow(false)
    override val audioLevel: StateFlow<Float> = MutableStateFlow(0f)
    override val view: View = previewCamera()

    override fun toggleVideo() { /* empty on purpose */ }
    override fun toggleAudio() { /* empty on purpose */ }
    override fun cycleCamera() { /* empty on purpose */ }
    override fun cycleCameraBlur() { /* empty on purpose */ }
    override fun clean() { /* empty on purpose */ }
}
