package com.vonage.android.kotlin.model

import android.view.View
import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.sdk.VonageSession
import com.vonage.android.kotlin.sdk.VonageStream
import com.vonage.android.kotlin.sdk.VonageSubscriber
import com.vonage.android.kotlin.sdk.VonageSubscriberStreamListener
import com.vonage.android.kotlin.sdk.VonageSubscriberVideoListener
import com.vonage.android.kotlin.sdk.VonageVideoType
import com.vonage.android.kotlin.ext.mapTalking
import com.vonage.android.kotlin.ext.movingAverage
import com.vonage.logger.vonageLogger
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Represents a remote participant (subscriber) in the video call.
 *
 * Manages the subscriber's stream state, audio level tracking, and video visibility.
 *
 * @param vonageSubscriber The wrapped Vonage Subscriber instance
 */
@Stable
data class ParticipantState(
    val vonageSubscriber: VonageSubscriber,
) : Participant {

    override val id: String = vonageSubscriber.stream.streamId

    override val connectionId: String = vonageSubscriber.stream.connection.connectionId

    override val isPublisher: Boolean = false

    override val creationTime: Long = vonageSubscriber.stream.creationTime

    override val videoSource: VideoSource = vonageSubscriber.stream.toVideoSource()

    override val isScreenShare: Boolean
        get() = videoSource == VideoSource.SCREEN

    override val name: String = vonageSubscriber.stream.name

    private val _isMicEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(vonageSubscriber.stream.hasAudio)
    override val isMicEnabled: StateFlow<Boolean> = _isMicEnabled

    private val _isCameraEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(vonageSubscriber.stream.hasVideo)
    override val isCameraEnabled: StateFlow<Boolean> = _isCameraEnabled

    private val _audioLevel: MutableStateFlow<Float> = MutableStateFlow(0F)
    override val audioLevel: StateFlow<Float> = _audioLevel

    private val _isTalking: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isTalking: StateFlow<Boolean> = _isTalking

    override val view: View = vonageSubscriber.view

    private val logTag = "Subscriber[$id]"

    private val _videoStats: MutableStateFlow<SubscriberVideoStats?> = MutableStateFlow(null)
    val videoStats: StateFlow<SubscriberVideoStats?> = _videoStats

    private val _audioStats: MutableStateFlow<SubscriberAudioStats?> = MutableStateFlow(null)
    val audioStats: StateFlow<SubscriberAudioStats?> = _audioStats

    internal val stream: VonageStream = vonageSubscriber.stream

    override fun changeVisibility(visible: Boolean) {
        when (visible) {
            // Use the live _isCameraEnabled state rather than the stale stream snapshot
            // so that toggling visibility after a remote camera-off event is correct.
            true  -> vonageSubscriber.subscribeToVideo = _isCameraEnabled.value
            false -> vonageSubscriber.subscribeToVideo = false
        }
    }

    /**
     * Updates camera and microphone state from a session-level stream-property change.
     * Called by [com.vonage.android.kotlin.Call] when the remote publisher toggles their
     * camera or microphone via [com.vonage.android.kotlin.sdk.VonageSessionListener.onStreamPropertyChanged].
     */
    internal fun updateStreamProperties(hasVideo: Boolean, hasAudio: Boolean) {
        _isCameraEnabled.value = hasVideo
        _isMicEnabled.value    = hasAudio
    }

    suspend fun setup() {
        vonageSubscriber.setStreamListener(object : VonageSubscriberStreamListener {
            override fun onReconnected() {
                vonageLogger.d(logTag, "Subscriber reconnected")
            }

            override fun onDisconnected() {
                vonageLogger.d(logTag, "Subscriber disconnected")
            }
        })
        vonageSubscriber.setVideoListener(object : VonageSubscriberVideoListener {
            override fun onVideoEnabled(reason: String) {
                vonageLogger.d(logTag, "Subscriber video enabled, reason=$reason")
                // Ignore the "subscribe" reason: it fires when we locally set
                // subscribeToVideo = true as a bandwidth optimisation and must not
                // be treated as the remote publisher re-enabling their camera.
                //if (reason != VIDEO_REASON_SUBSCRIBE) {
                    _isCameraEnabled.value = true
                //}
            }

            override fun onVideoDisabled(reason: String) {
                vonageLogger.d(logTag, "Subscriber video disabled, reason=$reason")
                // Ignore the "subscribe" reason: it fires when we locally set
                // subscribeToVideo = false (scroll-based visibility optimisation)
                // and must not corrupt the remote camera-state flag.
                //if (reason != VIDEO_REASON_SUBSCRIBE) {
                    _isCameraEnabled.value = false
                //}
            }

            override fun onVideoDataReceived() {
                vonageLogger.d(logTag, "Subscriber video data received")
            }

            override fun onVideoDisableWarning() {
                vonageLogger.d(logTag, "Subscriber video disable warning")
            }

            override fun onVideoDisableWarningLifted() {
                vonageLogger.d(logTag, "Subscriber video disable warning lifted")
            }
        })

        coroutineScope {
            launch {
                vonageSubscriber.observeVideoStats()
                    .collect { _videoStats.value = it }
            }
            launch {
                vonageSubscriber.observeAudioStats()
                    .collect { _audioStats.value = it }
            }
            vonageSubscriber.observeAudioLevel()
                .movingAverage(windowSize = 5)
                .distinctUntilChanged()
                .onEach { audioLevel ->
                    _audioLevel.emit(audioLevel)
                }
                .mapTalking()
                .collect { isTalking ->
                    _isTalking.value = isTalking
                }
        }
    }

    override fun clean(session: VonageSession) {
        vonageSubscriber.setVideoListener(null)
        vonageSubscriber.setStreamListener(null)
        vonageSubscriber.setVideoStatsListener(null)
        vonageSubscriber.setAudioStatsListener(null)
        vonageSubscriber.setAudioLevelListener(null)
        session.unsubscribe(vonageSubscriber)
    }

    @Stable
    data class SubscriberVideoStats(
        val videoPacketsReceived: Int,
        val videoPacketsLost: Int,
        val videoBytesReceived: Int,
        val width: Int,
        val height: Int,
        val codec: String,
        val decodedFrameRate: Double,
        val bitrate: Long,
        val freezeCount: Long,
        val totalFreezesDuration: Long,
        val estimatedBandwidthInBps: Long?,
    )

    @Stable
    data class SubscriberAudioStats(
        val audioPacketsReceived: Int,
        val audioPacketsLost: Int,
        val audioBytesReceived: Int,
        val estimatedBandwidthInBps: Long?,
    )
}

// region Extension helpers

private const val DEBOUNCE_SUBSCRIBER_AUDIO_LEVEL_MILLIS = 100L

/**
 * Reason string fired by the OpenTok SDK when [VonageSubscriber.subscribeToVideo] is changed
 * locally (e.g. the scroll-based bandwidth-optimisation path).
 * Events carrying this reason must NOT be used to update remote camera state.
 */
private const val VIDEO_REASON_SUBSCRIBE = "subscribe"

private fun VonageStream.toVideoSource(): VideoSource = when (videoType) {
    VonageVideoType.CAMERA -> VideoSource.CAMERA
    VonageVideoType.SCREEN, VonageVideoType.CUSTOM -> VideoSource.SCREEN
}

@OptIn(FlowPreview::class)
internal fun VonageSubscriber.observeAudioLevel(): Flow<Float> = callbackFlow {
    setAudioLevelListener { level ->
        if (isActive) {
            trySend(level)
        }
    }
    awaitClose { setAudioLevelListener(null) }
}
    .conflate()
    .sample(DEBOUNCE_SUBSCRIBER_AUDIO_LEVEL_MILLIS)

internal fun VonageSubscriber.observeVideoStats(): Flow<ParticipantState.SubscriberVideoStats> =
    callbackFlow {
        setVideoStatsListener { stats ->
            trySend(
                ParticipantState.SubscriberVideoStats(
                    videoPacketsReceived = stats.videoPacketsReceived,
                    videoPacketsLost = stats.videoPacketsLost,
                    videoBytesReceived = stats.videoBytesReceived,
                    width = stats.width,
                    height = stats.height,
                    codec = stats.codec,
                    decodedFrameRate = stats.decodedFrameRate,
                    bitrate = stats.bitrate,
                    freezeCount = stats.freezeCount,
                    totalFreezesDuration = stats.totalFreezesDuration,
                    estimatedBandwidthInBps = stats.estimatedBandwidthInBps,
                ),
            )
        }
        awaitClose { setVideoStatsListener(null) }
    }

internal fun VonageSubscriber.observeAudioStats(): Flow<ParticipantState.SubscriberAudioStats> =
    callbackFlow {
        setAudioStatsListener { stats ->
            trySend(
                ParticipantState.SubscriberAudioStats(
                    audioPacketsReceived = stats.audioPacketsReceived,
                    audioPacketsLost = stats.audioPacketsLost,
                    audioBytesReceived = stats.audioBytesReceived,
                    estimatedBandwidthInBps = stats.estimatedBandwidthInBps,
                ),
            )
        }
        awaitClose { setAudioStatsListener(null) }
    }

// endregion
