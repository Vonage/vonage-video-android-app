package com.vonage.android.kotlin.model

import android.view.View
import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.sdk.VonageBitratePreset
import com.vonage.android.kotlin.sdk.VonageBlurLevel
import com.vonage.android.kotlin.sdk.VonageCameraListener
import com.vonage.android.kotlin.sdk.VonageDegradationPref
import com.vonage.android.kotlin.sdk.VonageError
import com.vonage.android.kotlin.sdk.VonageMuteListener
import com.vonage.android.kotlin.sdk.VonageNoiseSuppression
import com.vonage.android.kotlin.sdk.VonagePublisher
import com.vonage.android.kotlin.sdk.VonagePublisherKitListener
import com.vonage.android.kotlin.sdk.VonagePublisherVideoListener
import com.vonage.android.kotlin.sdk.VonageSession
import com.vonage.android.kotlin.sdk.VonageStream
import com.vonage.android.kotlin.sdk.VonageVideoType
import com.vonage.android.kotlin.ext.movingAverage
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.internal.SpeakingWhileMutedDetector
import com.vonage.logger.vonageLogger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Represents the local publisher (current user's camera/screen) in the video call.
 *
 * Manages the publisher's stream state, camera controls, video effects, and audio monitoring.
 *
 * @param publisherId Unique identifier for this publisher
 * @param vonagePublisher The wrapped Vonage Publisher instance
 * @param initialVideoEffect The video effect to set on the publisher at creation time
 */
@Stable
data class PublisherState(
    private val publisherId: String,
    val vonagePublisher: VonagePublisher,
    override val captureInfoLabel: String = "",
    private val initialVideoEffect: VideoEffect = VideoEffect.None,
) : PublisherParticipant {

    override val id: String = publisherId
    override val connectionId: String = vonagePublisher.stream?.connection?.connectionId ?: ""
    private val logTag = "Publisher[$id]"
    override val creationTime: Long = vonagePublisher.stream?.creationTime ?: 0
    override val videoSource: VideoSource =
        vonagePublisher.stream?.toVideoSource() ?: VideoSource.CAMERA
    override val isScreenShare: Boolean
        get() = videoSource == VideoSource.SCREEN
    override val name: String = vonagePublisher.name
    override val view: View = vonagePublisher.view

    private val _isMicEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(vonagePublisher.publishAudio)
    override val isMicEnabled: StateFlow<Boolean> = _isMicEnabled

    private val _isCameraEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(vonagePublisher.publishVideo)
    override val isCameraEnabled: StateFlow<Boolean> = _isCameraEnabled

    /**
     * Tracks the user's explicit camera intent.
     * Only mutated by [toggleVideo]; never touched by [changeVisibility] or SDK callbacks.
     * This prevents the self-corruption that occurs when [changeVisibility] sets
     * `publishVideo = false` (causing `stream.hasVideo = false`), which would otherwise
     * make a subsequent `changeVisibility(true)` also set `publishVideo = false`.
     */
    private var userIntendedVideoOn: Boolean = vonagePublisher.publishVideo

    private val _audioLevel: MutableStateFlow<Float> = MutableStateFlow(0F)
    override val audioLevel: StateFlow<Float> = _audioLevel

    private val _isSpeaking: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isTalking: StateFlow<Boolean> = _isSpeaking

    private val _videoEffect: MutableStateFlow<VideoEffect> = MutableStateFlow(initialVideoEffect)
    override val videoEffect: StateFlow<VideoEffect> = _videoEffect

    private val _camera: MutableStateFlow<CameraType> = MutableStateFlow(CameraType.FRONT)
    override val camera: StateFlow<CameraType> = _camera

    private val _videoStats: MutableStateFlow<VideoStats?> = MutableStateFlow(null)
    val videoStats: StateFlow<VideoStats?> = _videoStats

    private val _audioStats: MutableStateFlow<AudioStats?> = MutableStateFlow(null)
    val audioStats: StateFlow<AudioStats?> = _audioStats

    private val _noiseSuppression: MutableStateFlow<NoiseSuppression> =
        MutableStateFlow(NoiseSuppression.DISABLED)
    override val noiseSuppression: StateFlow<NoiseSuppression> = _noiseSuppression

    private val speakingWhileMutedDetector = SpeakingWhileMutedDetector(
        isMicEnabled = _isMicEnabled,
        audioLevel = _audioLevel,
    )

    private val _isSpeakingWhileMuted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSpeakingWhileMuted: StateFlow<Boolean> = _isSpeakingWhileMuted

    override fun changeVisibility(visible: Boolean) {
        when (visible) {
            // Use userIntendedVideoOn instead of stream?.hasVideo.
            // stream.hasVideo is dynamic: it becomes false when publishVideo is set to false,
            // so reading it here would permanently lock the publisher video off.
            true  -> vonagePublisher.publishVideo = userIntendedVideoOn
            false -> vonagePublisher.publishVideo = false
        }
    }

    override fun toggleVideo() {
        // Toggle userIntendedVideoOn, not vonagePublisher.publishVideo.
        // publishVideo may be temporarily false due to a changeVisibility(false) call;
        // toggling it directly would invert the wrong value.
        userIntendedVideoOn = !userIntendedVideoOn
        vonagePublisher.publishVideo = userIntendedVideoOn
        _isCameraEnabled.update { userIntendedVideoOn }
        if (userIntendedVideoOn) applyVideoEffect(_videoEffect.value)
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
        vonagePublisher.toggleNoiseSuppression(currentValue.toVonageNoiseSuppression())
            .onSuccess { toggledValue ->
                _noiseSuppression.value = toggledValue.toNoiseSuppression()
            }
    }

    /**
     * Applies a video bitrate configuration to the publisher at runtime.
     *
     * @param config The bitrate configuration to apply
     */
    fun applyVideoBitrate(config: VideoBitrateConfig) {
        vonagePublisher.applyVideoBitrate(config.preset.toVonageBitratePreset(), config.maxBitrate)
        vonageLogger.d(
            logTag,
            "Applied bitrate: preset=${config.preset.label}, max=${config.maxBitrate}"
        )
    }

    fun applyDegradationPreference(preference: DegradationPreference) {
        vonagePublisher.applyDegradationPreference(preference.toVonageDegradationPref())
        vonageLogger.d(logTag, "Applied degradation preference: ${preference.label}")
    }

    override fun reinitializeRenderer() {
        vonagePublisher.reinitializeRenderer()
    }

    @Stable
    data class VideoStats(
        val duration: Double,
        val videoPacketsSent: Long,
        val videoPacketsLost: Long,
        val videoBytesSent: Long,
        val estimatedBandwidthInBps: Long,
        val videoLayerStats: ImmutableList<VideoLayerStats>,
    )

    @Stable
    data class VideoLayerStats(
        val height: Int,
        val width: Int,
        val codec: String,
        val encodedFrameRate: Double,
        val qualityLimitationReason: String,
        val scalabilityMode: String?,
        val bitrate: Long,
        val totalBitrate: Long,
    )

    @Stable
    data class AudioStats(
        val duration: Double,
        val audioPacketsSent: Long,
        val audioPacketsLost: Long,
        val audioBytesSent: Long,
        val estimatedBandwidthInBps: Long,
    )

    suspend fun setup() {
        vonagePublisher.setVideoListener(object : VonagePublisherVideoListener {
            override fun onVideoEnabled(reason: String) {
                vonageLogger.d(logTag, "Publisher video enabled - $reason")
                _isCameraEnabled.value = true
            }

            override fun onVideoDisabled(reason: String) {
                vonageLogger.d(logTag, "Publisher video disabled - $reason")
                _isCameraEnabled.value = false
            }

            override fun onVideoDisableWarning() {
                vonageLogger.d(logTag, "Publisher video disable warning")
            }

            override fun onVideoDisableWarningLifted() {
                vonageLogger.d(logTag, "Publisher video disable warning lifted")
            }
        })
        vonagePublisher.setPublisherListener(object : VonagePublisherKitListener {
            override fun onStreamCreated(stream: VonageStream) {
                vonageLogger.d(logTag, "Publisher stream created")
            }

            override fun onStreamDestroyed(stream: VonageStream) {
                vonageLogger.d(logTag, "Publisher stream destroyed")
            }

            override fun onError(error: VonageError) {
                vonageLogger.e(logTag, "Publisher error ${error.message}")
            }
        })
        vonagePublisher.setMuteListener(VonageMuteListener {
            vonageLogger.d(logTag, "Publisher mute forced")
            _isMicEnabled.value = false
        })
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

        coroutineScope {
            launch {
                vonagePublisher.observeVideoStats()
                    .collect { _videoStats.value = it }
            }
            launch {
                vonagePublisher.observeAudioStats()
                    .collect { _audioStats.value = it }
            }
            launch {
                speakingWhileMutedDetector.isSpeakingWhileMuted
                    .collect { _isSpeakingWhileMuted.value = it }
            }
            vonagePublisher.observeAudioLevel()
                .movingAverage(windowSize = 2)
                .distinctUntilChanged()
                .collect { audioLevel ->
                    _audioLevel.value = audioLevel
                }
        }
    }

    override fun clean(session: VonageSession) {
        vonagePublisher.setVideoListener(null)
        vonagePublisher.setMuteListener(null)
        vonagePublisher.setPublisherListener(null)
        session.unpublish(vonagePublisher)
    }

    override fun clean() {
        vonagePublisher.setVideoListener(null)
        vonagePublisher.setMuteListener(null)
        vonagePublisher.setPublisherListener(null)
    }
}

// region Extension helpers

private fun VonageStream.toVideoSource(): VideoSource = when (videoType) {
    VonageVideoType.CAMERA -> VideoSource.CAMERA
    VonageVideoType.SCREEN, VonageVideoType.CUSTOM -> VideoSource.SCREEN
}

internal fun VonagePublisher.observeAudioLevel(): Flow<Float> = callbackFlow {
    setAudioLevelListener { level ->
        trySend(level)
    }
    awaitClose { setAudioLevelListener(null) }
}

internal fun VonagePublisher.observeVideoStats(): Flow<PublisherState.VideoStats> =
    callbackFlow {
        setVideoStatsListener { entries ->
            if (entries.isNotEmpty()) {
                val s = entries[0]
                trySend(
                    PublisherState.VideoStats(
                        duration = (s.timeStamp - s.startTime) / 1000,
                        videoPacketsSent = s.videoPacketsSent,
                        videoPacketsLost = s.videoPacketsLost,
                        videoBytesSent = s.videoBytesSent,
                        estimatedBandwidthInBps = s.connectionEstimatedBandwidth,
                        videoLayerStats = s.videoLayers.map { layer ->
                            PublisherState.VideoLayerStats(
                                height = layer.height,
                                width = layer.width,
                                codec = layer.codec,
                                encodedFrameRate = layer.encodedFrameRate,
                                qualityLimitationReason = layer.qualityLimitationReason,
                                scalabilityMode = layer.scalabilityMode,
                                bitrate = layer.bitrate,
                                totalBitrate = layer.totalBitrate,
                            )
                        }.toImmutableList(),
                    ),
                )
            }
        }
        awaitClose { setVideoStatsListener(null) }
    }

internal fun VonagePublisher.observeAudioStats(): Flow<PublisherState.AudioStats> =
    callbackFlow {
        setAudioStatsListener { entries ->
            if (entries.isNotEmpty()) {
                val s = entries[0]
                trySend(
                    PublisherState.AudioStats(
                        duration = (s.timeStamp - s.startTime) / 1000,
                        audioPacketsSent = s.audioPacketsSent,
                        audioPacketsLost = s.audioPacketsLost,
                        audioBytesSent = s.audioBytesSent,
                        estimatedBandwidthInBps = s.connectionEstimatedBandwidth,
                    ),
                )
            }
        }
        awaitClose { setAudioStatsListener(null) }
    }

internal fun NoiseSuppression.toVonageNoiseSuppression(): VonageNoiseSuppression = when (this) {
    NoiseSuppression.ENABLED -> VonageNoiseSuppression.ENABLED
    NoiseSuppression.DISABLED -> VonageNoiseSuppression.DISABLED
}

internal fun VonageNoiseSuppression.toNoiseSuppression(): NoiseSuppression = when (this) {
    VonageNoiseSuppression.ENABLED -> NoiseSuppression.ENABLED
    VonageNoiseSuppression.DISABLED -> NoiseSuppression.DISABLED
}

internal fun VideoBitratePreset.toVonageBitratePreset(): VonageBitratePreset =
    when (this) {
        VideoBitratePreset.DEFAULT -> VonageBitratePreset.DEFAULT
        VideoBitratePreset.BW_SAVER -> VonageBitratePreset.BW_SAVER
        VideoBitratePreset.EXTRA_BW_SAVER -> VonageBitratePreset.EXTRA_BW_SAVER
        VideoBitratePreset.CUSTOM -> VonageBitratePreset.CUSTOM
    }

internal fun DegradationPreference.toVonageDegradationPref(): VonageDegradationPref =
    when (this) {
        DegradationPreference.NOT_SET -> VonageDegradationPref.NOT_SET
        DegradationPreference.MAINTAIN_FRAME_RATE_AND_RESOLUTION -> VonageDegradationPref.MAINTAIN_FRAME_RATE_AND_RESOLUTION
        DegradationPreference.MAINTAIN_FRAME_RATE -> VonageDegradationPref.MAINTAIN_FRAME_RATE
        DegradationPreference.MAINTAIN_RESOLUTION -> VonageDegradationPref.MAINTAIN_RESOLUTION
        DegradationPreference.BALANCED -> VonageDegradationPref.BALANCED
    }

// endregion
