package com.vonage.android.kotlin.model

import android.view.View
import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.VonageCameraListener
import com.vonage.android.kotlin.VonageError
import com.vonage.android.kotlin.VonagePublisher
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.internal.MicVolumeListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update

/**
 * Represents a preview-only publisher used before joining a call.
 *
 * Provides camera preview functionality with controls for testing video and audio
 * settings before entering a video session. Uses MicVolumeListener for direct
 * microphone monitoring since it's not connected to a session.
 *
 * @param vonagePublisher The wrapped Vonage Publisher instance for preview
 */
@Stable
data class PreviewPublisherState(
    private val vonagePublisher: VonagePublisher,
    override val captureInfoLabel: String = "",
) : PublisherParticipant {

    private val micVolumeListener by lazy { MicVolumeListener() }

    override val id: String = "preview-publisher"
    override val connectionId: String = "preview-publisher-connection-id"
    override val creationTime: Long = 0
    override val isScreenShare: Boolean = false
    override val videoSource: VideoSource = VideoSource.CAMERA
    override val name: String = vonagePublisher.name
    override val view: View = vonagePublisher.view

    private val _isMicEnabled: MutableStateFlow<Boolean> = MutableStateFlow(vonagePublisher.publishAudio)
    override val isMicEnabled: StateFlow<Boolean> = _isMicEnabled

    private val _isCameraEnabled: MutableStateFlow<Boolean> = MutableStateFlow(vonagePublisher.publishVideo)
    override val isCameraEnabled: StateFlow<Boolean> = _isCameraEnabled

    private val _audioLevel: MutableStateFlow<Float> = MutableStateFlow(0F)
    override val audioLevel: StateFlow<Float> = _audioLevel

    override val isTalking: StateFlow<Boolean> = MutableStateFlow(false)

    private val _blurLevel: MutableStateFlow<BlurLevel> = MutableStateFlow(BlurLevel.NONE)
    override val blurLevel: StateFlow<BlurLevel> = _blurLevel

    private val _camera: MutableStateFlow<CameraType> = MutableStateFlow(CameraType.FRONT)
    override val camera: StateFlow<CameraType> = _camera

    override fun toggleVideo() {
        vonagePublisher.publishVideo = vonagePublisher.publishVideo.toggle()
        _isCameraEnabled.update { vonagePublisher.publishVideo }
    }

    override fun toggleAudio() {
        vonagePublisher.publishAudio = vonagePublisher.publishAudio.toggle()
        _isMicEnabled.update { vonagePublisher.publishAudio }
    }

    override fun cycleCameraBlur() {
        var index = BlurLevel.entries.first { it == _blurLevel.value }.ordinal
        val newLevel = BlurLevel by ++index
        vonagePublisher.applyBlur(newLevel.toVonageBlurLevel())
        _blurLevel.value = newLevel
    }

    override fun cycleCamera() {
        vonagePublisher.cycleCamera()
    }

    suspend fun setup() {
        vonagePublisher.setCameraListener(object : VonageCameraListener {
            override fun onCameraChanged(cameraIndex: Int) {
                CameraType.fromInt(cameraIndex)?.let { cameraType ->
                    _camera.update { cameraType }
                }
            }
            override fun onCameraError(error: VonageError) {
                // No-op for now
            }
        })
        micVolumeListener.start()
            .distinctUntilChanged()
            .collectLatest { _audioLevel.value = it }
    }

    override fun clean() {
        micVolumeListener.stop()
    }
}
