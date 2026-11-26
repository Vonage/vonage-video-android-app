package com.vonage.android.kotlin.model

import android.view.View
import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.sdk.VonageSession
import com.vonage.android.kotlin.sdk.VonageStream
import com.vonage.android.kotlin.sdk.VonageSubscriber
import com.vonage.android.kotlin.sdk.VonageSubscriberStreamListener
import com.vonage.android.kotlin.sdk.VonageSubscriberVideoListener
import com.vonage.android.kotlin.sdk.VonageVideoSize
import com.vonage.android.kotlin.sdk.VonageVideoType
import com.vonage.android.kotlin.ext.mapTalking
import com.vonage.android.kotlin.ext.movingAverage
import com.vonage.android.kotlin.internal.SubscriberVideoQualityPolicy
import com.vonage.android.kotlin.internal.SubscriberVideoTier
import com.vonage.logger.vonageLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private val _networkCondition: MutableStateFlow<NetworkCondition?> = MutableStateFlow(null)
    val networkCondition: StateFlow<NetworkCondition?> = _networkCondition

    internal val stream: VonageStream = vonageSubscriber.stream

    override fun changeVisibility(visible: Boolean) {
        when (visible) {
            // Use the live _isCameraEnabled state rather than the stale stream snapshot
            // so that toggling visibility after a remote camera-off event is correct.
            true  -> vonageSubscriber.subscribeToVideo = _isCameraEnabled.value
            false -> vonageSubscriber.subscribeToVideo = false
        }
    }

    override fun reinitializeRenderer() {
        vonageSubscriber.reinitializeRenderer()
    }

    /** Latest rendered view size in px, `null` until the view is first laid out. */
    private val renderedSize = MutableStateFlow<Pair<Int, Int>?>(null)

    private val _videoTier = MutableStateFlow(SubscriberVideoTier.FULL)

    /** Receive-side quality tier currently requested for this stream. */
    internal val videoTier: StateFlow<SubscriberVideoTier> = _videoTier

    /** Resolution last sent to the SDK, `null` while no preference is set. */
    private var appliedResolution: VonageVideoSize? = null

    override fun onRenderedSizeChanged(width: Int, height: Int) {
        if (width > 0 && height > 0) renderedSize.value = width to height
    }

    /**
     * Applies [SubscriberVideoQualityPolicy] whenever the rendered size or the source aspect
     * ratio settles. The source size comes from video stats, which usually arrive after the
     * first layout, and changes when the remote device rotates.
     * The subscriber starts with no preference (full quality) until the first size arrives.
     */
    @OptIn(FlowPreview::class)
    private suspend fun observeRenderedSize() {
        combine(renderedSize.filterNotNull(), sourceSize()) { rendered, source -> rendered to source }
            .debounce(SubscriberVideoQualityPolicy.SETTLE_MILLIS)
            .collect { (rendered, source) ->
                val (width, height) = rendered
                val tier = SubscriberVideoQualityPolicy.tierFor(
                    viewWidth = width,
                    viewHeight = height,
                    sourceWidth = source?.first ?: 0,
                    sourceHeight = source?.second ?: 0,
                    isScreenShare = isScreenShare,
                    current = _videoTier.value,
                )
                val sourceIsPortrait = source != null && source.second > source.first
                val resolution = tier.preferredResolution(sourceIsPortrait)
                // A rotation can keep the tier but flip the requested layer's orientation.
                if (tier == _videoTier.value && resolution == appliedResolution) return@collect
                // SDK setters stay on the main thread, like subscriber creation in Call.
                withContext(Dispatchers.Main) {
                    vonageSubscriber.setPreferredResolution(resolution)
                    vonageSubscriber.setPreferredFrameRate(tier.frameRate)
                }
                appliedResolution = resolution
                _videoTier.value = tier
                vonageLogger.d(logTag, "Video tier=$tier rendered=${width}x$height")
            }
    }

    /**
     * Received video size, `null` while unknown. Only aspect-ratio changes are emitted:
     * every layer switch changes the received size, and reacting to that would re-evaluate
     * the tier on each switch.
     */
    private fun sourceSize(): Flow<Pair<Int, Int>?> =
        _videoStats
            .map { stats -> stats?.takeIf { it.width > 0 && it.height > 0 }?.let { it.width to it.height } }
            .distinctUntilChanged { old, new -> old?.aspectKey() == new?.aspectKey() }

    /**
     * Updates camera and microphone state from a session-level stream-property change.
     * Called by [com.vonage.android.kotlin.Call] when the remote publisher toggles their
     * camera or microphone via [com.vonage.android.kotlin.sdk.VonageSessionListener.onStreamPropertyChanged].
     */
    internal fun updateStreamProperties(hasVideo: Boolean, hasAudio: Boolean) {
        _isCameraEnabled.value = hasVideo
        _isMicEnabled.value    = hasAudio
    }

    /**
     * Registers stream and video listeners synchronously. Must be called before the participant
     * is added to the participants map and made visible in the UI, so that SDK callbacks such as
     * [VonageSubscriberVideoListener.onVideoEnabled] are never missed.
     *
     * Call [setup] afterwards (in a separate coroutine) to start the long-running audio-level
     * and stats observations.
     */
    internal fun registerListeners() {
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
                _isCameraEnabled.value = true
            }

            override fun onVideoDisabled(reason: String) {
                vonageLogger.d(logTag, "Subscriber video disabled, reason=$reason")
                _isCameraEnabled.value = false
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
    }

    /**
     * Starts long-running audio-level and stats observations. Suspends indefinitely.
     * Listeners must be registered via [registerListeners] before calling this.
     */
    suspend fun setup() {
        coroutineScope {
            launch {
                vonageSubscriber.observeVideoStats()
                    .collect { _videoStats.value = it }
            }
            launch {
                vonageSubscriber.observeAudioStats()
                    .collect { _audioStats.value = it }
            }
            launch {
                observeNetworkCondition(vonageSubscriber::setNetworkConditionListener)
                    .collect { _networkCondition.value = it }
            }
            launch { observeRenderedSize() }
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
        vonageSubscriber.setNetworkConditionListener(null)
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

private const val ASPECT_KEY_SCALE = 100

/** Aspect ratio rounded to two decimals, equal for every simulcast layer of one source. */
private fun Pair<Int, Int>.aspectKey(): Int = first * ASPECT_KEY_SCALE / second

private const val DEBOUNCE_SUBSCRIBER_AUDIO_LEVEL_MILLIS = 100L

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
