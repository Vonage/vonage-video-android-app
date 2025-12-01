package com.vonage.android.kotlin.model

import android.view.View
import androidx.compose.runtime.Stable
import com.opentok.android.OpentokError
import com.opentok.android.Publisher
import com.vonage.android.kotlin.ext.cycleBlur
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
 * @param publisher The OpenTok Publisher instance for preview
 */
@Stable
data class PreviewPublisherState(
    private val publisher: Publisher,
) : PublisherParticipant,
    Publisher.CameraListener {

    private val micVolumeListener by lazy { MicVolumeListener() }

    override val id: String = "preview-publisher"
    override val connectionId: String = "preview-publisher-connection-id"
    override val creationTime: Long = 0
    override val videoSource: VideoSource = VideoSource.CAMERA
    override val name: String = publisher.name
    override val view: View = publisher.view

    private val _isMicEnabled: MutableStateFlow<Boolean> = MutableStateFlow(publisher.publishAudio)
    override val isMicEnabled: StateFlow<Boolean> = _isMicEnabled

    private val _isCameraEnabled: MutableStateFlow<Boolean> = MutableStateFlow(publisher.publishVideo)
    override val isCameraEnabled: StateFlow<Boolean> = _isCameraEnabled

    private val _audioLevel: MutableStateFlow<Float> = MutableStateFlow(0F)
    override val audioLevel: StateFlow<Float> = _audioLevel

    override val isTalking: StateFlow<Boolean> = MutableStateFlow(false)

    private val _blurLevel: MutableStateFlow<BlurLevel> = MutableStateFlow(BlurLevel.NONE)
    override val blurLevel: StateFlow<BlurLevel> = _blurLevel

    private val _camera: MutableStateFlow<CameraType> = MutableStateFlow(CameraType.BACK)
    override val camera: StateFlow<CameraType> = _camera

    override fun toggleVideo() {
        publisher.publishVideo = publisher.publishVideo.toggle()
        _isCameraEnabled.update { publisher.publishVideo }
    }

    override fun toggleAudio() {
        publisher.publishAudio = publisher.publishAudio.toggle()
        _isMicEnabled.update { publisher.publishAudio }
    }

    override fun cycleCameraBlur() {
        publisher.cycleBlur(_blurLevel.value) {
            _blurLevel.update { it }
        }
    }

    override fun cycleCamera() {
        publisher.cycleCamera()
    }

    /**
     * Initializes preview monitoring including camera listener and microphone volume.
     *
     * Must be called before using the preview to start audio level monitoring.
     */
    suspend fun setup() {
        publisher.setCameraListener(this)
        micVolumeListener.start()
            .distinctUntilChanged()
            .collectLatest { _audioLevel.value = it }
    }

    override fun clean() {
        micVolumeListener.stop()
    }

    override fun onCameraChanged(publisher: Publisher, cameraIndex: Int) {
        CameraType.fromInt(cameraIndex)?.let { cameraType ->
            _camera.update { cameraType }
        }
    }

    override fun onCameraError(publisher: Publisher, error: OpentokError) {
        // No-op for now
    }
}
