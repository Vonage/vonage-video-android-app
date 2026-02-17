package com.vonage.android.kotlin.model

import android.view.View
import androidx.compose.runtime.Stable
import com.opentok.android.Session
import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import com.vonage.android.kotlin.ext.mapTalking
import com.vonage.android.kotlin.ext.movingAverage
import com.vonage.android.kotlin.ext.name
import com.vonage.android.kotlin.ext.observeAudioLevel
import com.vonage.android.kotlin.ext.toParticipantType
import com.vonage.logger.vonageLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach

/**
 * Represents a remote participant (subscriber) in the video call.
 *
 * Manages the subscriber's stream state, audio level tracking, and video visibility.
 * Implements listener interfaces to react to stream and video state changes.
 *
 * @param subscriber The OpenTok Subscriber instance for this remote participant
 */
@Stable
data class ParticipantState(
    val subscriber: Subscriber,
) : Participant,
    SubscriberKit.StreamListener,
    SubscriberKit.VideoListener {

    override val id: String = subscriber.stream.streamId

    override val connectionId: String = subscriber.stream.connection.connectionId

    override val isPublisher: Boolean = false

    override val creationTime: Long = subscriber.stream.creationTime.time

    override val videoSource: VideoSource = subscriber.stream.toParticipantType()

    override val isScreenShare: Boolean
        get() = videoSource == VideoSource.SCREEN

    override val name: String = subscriber.name()

    private val _isMicEnabled: MutableStateFlow<Boolean> = MutableStateFlow(subscriber.stream.hasAudio())
    override val isMicEnabled: StateFlow<Boolean> = _isMicEnabled

    private val _isCameraEnabled: MutableStateFlow<Boolean> = MutableStateFlow(subscriber.stream.hasVideo())
    override val isCameraEnabled: StateFlow<Boolean> = _isCameraEnabled

    private val _audioLevel: MutableStateFlow<Float> = MutableStateFlow(0F)
    override val audioLevel: StateFlow<Float> = _audioLevel

    private val _isTalking: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isTalking: StateFlow<Boolean> = _isTalking

    override val view: View = subscriber.view

    private val logTag = "Subscriber[$id]"

    override fun changeVisibility(visible: Boolean) {
        when (visible) {
            true -> subscriber.subscribeToVideo = subscriber.stream.hasVideo()
            false -> subscriber.subscribeToVideo = false
        }
    }

    /**
     * Initializes audio level monitoring and talking detection.
     *
     * Sets up listeners and starts collecting audio level data with moving average
     * to determine when the participant is speaking.
     */
    suspend fun setup() {
        subscriber.setStreamListener(this)
        subscriber.setVideoListener(this)

        subscriber.observeAudioLevel()
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

    override fun clean(session: Session) {
        subscriber.setVideoListener(null)
        subscriber.setStreamListener(null)
        subscriber.setAudioLevelListener(null)
        session.unsubscribe(subscriber)
    }

    override fun onReconnected(subscriber: SubscriberKit) {
        vonageLogger.d(logTag, "Subscriber reconnected")
    }

    override fun onDisconnected(subscriber: SubscriberKit) {
        vonageLogger.d(logTag, "Subscriber disconnected")
    }

    override fun onAudioDisabled(subscriber: SubscriberKit) {
        vonageLogger.d(logTag, "Subscriber audio disabled")
        _isMicEnabled.value = false
    }

    override fun onAudioEnabled(subscriber: SubscriberKit) {
        vonageLogger.d(logTag, "Subscriber audio enabled")
        _isMicEnabled.value = true
    }

    override fun onVideoDataReceived(subscriber: SubscriberKit) {
        vonageLogger.d(logTag, "Subscriber video data received")
    }

    override fun onVideoDisabled(subscriber: SubscriberKit, reason: String) {
        vonageLogger.d(logTag, "Subscriber video disabled")
        _isCameraEnabled.value = false
    }

    override fun onVideoEnabled(subscriber: SubscriberKit, reason: String) {
        vonageLogger.d(logTag, "Subscriber video enabled")
        _isCameraEnabled.value = true
    }

    override fun onVideoDisableWarning(subscriber: SubscriberKit) {
        vonageLogger.d(logTag, "Subscriber video disable warning")
    }

    override fun onVideoDisableWarningLifted(subscriber: SubscriberKit) {
        vonageLogger.d(logTag, "Subscriber video disable warning lifted")
    }
}
