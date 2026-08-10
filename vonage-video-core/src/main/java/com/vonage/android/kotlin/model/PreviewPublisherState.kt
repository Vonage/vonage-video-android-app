package com.vonage.android.kotlin.model

import android.view.View
import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.sdk.VonageBlurLevel
import com.vonage.android.kotlin.sdk.VonageCameraListener
import com.vonage.android.kotlin.sdk.VonageError
import com.vonage.android.kotlin.sdk.VonagePublisher
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
 * @param initialVideoEffect The video effect to set on the publisher at creation time
 */
@Stable
data class PreviewPublisherState(
    private val vonagePublisher: VonagePublisher,
    override val captureInfoLabel: String = "",
    private val initialVideoEffect: VideoEffect = VideoEffect.None,
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

    private val _videoEffect: MutableStateFlow<VideoEffect> = MutableStateFlow(initialVideoEffect)
    override val videoEffect: StateFlow<VideoEffect> = _videoEffect

    private val _camera: MutableStateFlow<CameraType> = MutableStateFlow(CameraType.FRONT)
    override val camera: StateFlow<CameraType> = _camera

    private val _noiseSuppression: MutableStateFlow<NoiseSuppression> = MutableStateFlow(NoiseSuppression.DISABLED)
    override val noiseSuppression: StateFlow<NoiseSuppression> = _noiseSuppression

    override fun toggleVideo() {
        vonagePublisher.publishVideo = vonagePublisher.publishVideo.toggle()
        _isCameraEnabled.update { vonagePublisher.publishVideo }
        if (vonagePublisher.publishVideo) applyVideoEffect(_videoEffect.value)
    }

    override fun toggleAudio() {
        vonagePublisher.publishAudio = vonagePublisher.publishAudio.toggle()
        _isMicEnabled.update { vonagePublisher.publishAudio }
    }

    override fun cycleCamera() {
        vonagePublisher.cycleCamera()
    }

    override fun applyVideoEffect(effect: VideoEffect) {
        when (effect) {
            VideoEffect.None -> vonagePublisher.applyBlur(VonageBlurLevel.NONE)
            VideoEffect.BlurLow -> vonagePublisher.applyBlur(VonageBlurLevel.LOW)
            VideoEffect.BlurHigh -> vonagePublisher.applyBlur(VonageBlurLevel.HIGH)
            is VideoEffect.BackgroundImage -> vonagePublisher.applyBackgroundImage(effect.imagePath)
        }
        _videoEffect.value = effect
    }

    override fun toggleNoiseSuppression() {
        val currentValue = _noiseSuppression.value
        val newValue = when (currentValue) {
            NoiseSuppression.DISABLED -> NoiseSuppression.ENABLED
            NoiseSuppression.ENABLED -> NoiseSuppression.DISABLED
        }
        vonagePublisher.toggleNoiseSuppression(newValue.toVonageNoiseSuppression())
            .onSuccess { _noiseSuppression.value = newValue }
    }

    /**
     * Initializes preview monitoring including camera listener and microphone volume.
     *
     * Must be called before using the preview to start audio level monitoring.
     */
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
