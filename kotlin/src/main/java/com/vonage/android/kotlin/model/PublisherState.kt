package com.vonage.android.kotlin.model

import android.util.Log
import android.view.View
import androidx.compose.runtime.Stable
import com.opentok.android.OpentokError
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.opentok.android.Session
import com.opentok.android.Stream
import com.vonage.android.kotlin.ext.cycleBlur
import com.vonage.android.kotlin.ext.movingAverage
import com.vonage.android.kotlin.ext.observeAudioLevel
import com.vonage.android.kotlin.ext.toParticipantType
import com.vonage.android.kotlin.ext.toggle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update

/**
 * Represents the local publisher (current user's camera/screen) in the video call.
 *
 * Manages the publisher's stream state, camera controls, blur effects, and audio monitoring.
 * Implements listener interfaces to react to publisher state changes.
 *
 * @param publisherId Unique identifier for this publisher (e.g., "publisher" or "publisher-screen")
 * @param publisher The OpenTok Publisher instance
 */
@Stable
data class PublisherState(
    private val publisherId: String,
    val publisher: Publisher,
) : PublisherParticipant,
    Publisher.CameraListener,
    PublisherKit.VideoListener,
    PublisherKit.PublisherListener,
    PublisherKit.MuteListener {

    override val id: String = publisherId
    override val connectionId: String = publisher.stream?.connection?.connectionId ?: ""
    private val logTag = "Publisher[$id]"
    override val creationTime: Long = publisher.stream?.creationTime?.time ?: 0
    override val videoSource: VideoSource = publisher.stream?.toParticipantType() ?: VideoSource.CAMERA
    override val name: String = publisher.stream?.name ?: ""
    override val view: View = publisher.view

    private val _isMicEnabled: MutableStateFlow<Boolean> = MutableStateFlow(publisher.publishAudio)
    override val isMicEnabled: StateFlow<Boolean> = _isMicEnabled

    private val _isCameraEnabled: MutableStateFlow<Boolean> = MutableStateFlow(publisher.publishVideo)
    override val isCameraEnabled: StateFlow<Boolean> = _isCameraEnabled

    private val _audioLevel: MutableStateFlow<Float> = MutableStateFlow(0F)
    override val audioLevel: StateFlow<Float> = _audioLevel

    private val _isSpeaking: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isTalking: StateFlow<Boolean> = _isSpeaking

    private val _blurLevel: MutableStateFlow<BlurLevel> = MutableStateFlow(BlurLevel.NONE)
    override val blurLevel: StateFlow<BlurLevel> = _blurLevel

    private val _camera: MutableStateFlow<CameraType> = MutableStateFlow(CameraType.BACK)
    override val camera: StateFlow<CameraType> = _camera

    override fun changeVisibility(visible: Boolean) {
        when (visible) {
            true -> publisher.publishVideo = publisher.stream.hasVideo()
            false -> publisher.publishVideo = false
        }
    }

    override fun toggleVideo() {
        publisher.publishVideo = publisher.publishVideo.toggle()
        _isCameraEnabled.update { publisher.publishVideo }
    }

    override fun toggleAudio() {
        publisher.publishAudio = publisher.publishAudio.toggle()
        _isMicEnabled.update { publisher.publishAudio }
    }

    override fun cycleCamera() {
        publisher.cycleCamera()
    }

    // TODO: Add to UI
    override fun cycleCameraBlur() {
        publisher.cycleBlur(_blurLevel.value) {
            _blurLevel.update { it }
        }
    }

    /**
     * Initializes publisher listeners and audio level monitoring.
     *
     * Sets up all necessary listeners and starts collecting audio level data.
     */
    suspend fun setup() {
        publisher.setVideoListener(this)
        publisher.setPublisherListener(this)
        publisher.setMuteListener(this)
        publisher.setCameraListener(this)

        publisher.observeAudioLevel()
            .movingAverage(windowSize = 2)
            .distinctUntilChanged()
            .collect { audioLevel ->
                _audioLevel.value = audioLevel
            }
    }

    override fun clean(session: Session) {
        publisher.setVideoListener(null)
        publisher.setMuteListener(null)
        publisher.setPublisherListener(null)
        session.unpublish(publisher)
    }

    override fun clean() {
        publisher.setVideoListener(null)
        publisher.setMuteListener(null)
        publisher.setPublisherListener(null)
    }

    override fun onVideoDisabled(publisher: PublisherKit, reason: String) {
        Log.d(logTag, "Publisher video disabled - $reason")
        _isCameraEnabled.value = false
    }

    override fun onVideoEnabled(publisher: PublisherKit, reason: String) {
        Log.d(logTag, "Publisher video enabled - $reason")
        _isCameraEnabled.value = true
    }

    override fun onVideoDisableWarning(publisher: PublisherKit) {
        Log.d(logTag, "Publisher video disable warning")
    }

    override fun onVideoDisableWarningLifted(publisher: PublisherKit) {
        Log.d(logTag, "Publisher video disable warning lifted")
    }

    override fun onStreamCreated(publisher: PublisherKit, stream: Stream) {
        Log.d(logTag, "Publisher stream created")
    }

    override fun onStreamDestroyed(publisher: PublisherKit, stream: Stream) {
        Log.d(logTag, "Publisher stream destroyed")
    }

    override fun onError(publisher: PublisherKit, error: OpentokError) {
        Log.e(logTag, "Publisher error ${error.message}")
    }

    override fun onMuteForced(publisher: PublisherKit) {
        Log.d(logTag, "Publisher mute forced")
        _isMicEnabled.value = false
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
