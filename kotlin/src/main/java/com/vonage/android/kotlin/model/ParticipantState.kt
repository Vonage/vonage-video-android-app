package com.vonage.android.kotlin.model

import android.util.Log
import android.view.View
import androidx.compose.runtime.Stable
import com.opentok.android.Session
import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import com.vonage.android.kotlin.ext.mapTalking
import com.vonage.android.kotlin.ext.movingAverage
import com.vonage.android.kotlin.ext.name
import com.vonage.android.kotlin.ext.observeAudioLevel
import com.vonage.android.kotlin.internal.toParticipantType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach

@Stable
data class ParticipantState(
    private val subscriber: Subscriber,
) : Participant,
    SubscriberKit.StreamListener,
    SubscriberKit.VideoListener {

    private val logTag = "Subscriber[$id]"

    override val id: String
        get() = subscriber.stream.streamId

    override val videoSource: VideoSource
        get() = subscriber.stream.toParticipantType()

    override val name: String
        get() = subscriber.name()

    private val _isMicEnabled: MutableStateFlow<Boolean> = MutableStateFlow(subscriber.stream.hasAudio())
    override val isMicEnabled: StateFlow<Boolean>
        get() = _isMicEnabled

    private val _isCameraEnabled: MutableStateFlow<Boolean> = MutableStateFlow(subscriber.stream.hasVideo())
    override val isCameraEnabled: StateFlow<Boolean>
        get() = _isCameraEnabled

    private val _visible: MutableStateFlow<Boolean>
        get() = MutableStateFlow(false)
    val visible: StateFlow<Boolean>
        get() = _visible

    private val _audioLevel: MutableStateFlow<Float> = MutableStateFlow(0F)
    override val audioLevel: StateFlow<Float>
        get() = _audioLevel

    private val _isTalking: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isTalking: StateFlow<Boolean>
        get() = _isTalking

    override val view: View = subscriber.view

    override fun changeVisibility(visible: Boolean) {
        when (visible) {
            true -> subscriber.subscribeToVideo = subscriber.stream.hasVideo()
            false -> subscriber.subscribeToVideo = false
        }
    }

    suspend fun setup() {
        subscriber.setStreamListener(this)
        subscriber.setVideoListener(this)

        subscriber.observeAudioLevel()
            .movingAverage(windowSize = 5)
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
        Log.d(logTag, "Subscriber reconnected")
    }

    override fun onDisconnected(subscriber: SubscriberKit) {
        Log.d(logTag, "Subscriber disconnected")
    }

    override fun onAudioDisabled(subscriber: SubscriberKit) {
        Log.d(logTag, "Subscriber audio disabled")
        _isMicEnabled.value = false
    }

    override fun onAudioEnabled(subscriber: SubscriberKit) {
        Log.d(logTag, "Subscriber audio enabled")
        _isMicEnabled.value = true
    }

    override fun onVideoDataReceived(subscriber: SubscriberKit) {
        Log.d(logTag, "Subscriber video data received")
    }

    override fun onVideoDisabled(subscriber: SubscriberKit, reason: String) {
        Log.d(logTag, "Subscriber video disabled")
        _isCameraEnabled.value = false
    }

    override fun onVideoEnabled(subscriber: SubscriberKit, reason: String) {
        Log.d(logTag, "Subscriber video enabled")
        _isCameraEnabled.value = true
    }

    override fun onVideoDisableWarning(subscriber: SubscriberKit) {
        Log.d(logTag, "Subscriber video disable warning")
    }

    override fun onVideoDisableWarningLifted(subscriber: SubscriberKit) {
        Log.d(logTag, "Subscriber video disable warning lifted")
    }
}
