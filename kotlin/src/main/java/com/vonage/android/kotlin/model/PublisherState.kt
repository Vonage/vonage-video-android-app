package com.vonage.android.kotlin.model

import android.util.Log
import android.view.View
import androidx.compose.runtime.Stable
import com.opentok.android.Publisher
import com.opentok.android.Session
import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import com.vonage.android.kotlin.ext.name
import com.vonage.android.kotlin.ext.observeAudioLevel
import com.vonage.android.kotlin.internal.toParticipantType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Stable
data class PublisherState(
    private val publisher: Publisher,
) : Participant,
    SubscriberKit.StreamListener,
    SubscriberKit.VideoListener {

    private val TAG = "Publisher[$id]"

    override val id: String
        get() = publisher.stream.streamId

    override val videoSource: VideoSource
        get() = publisher.stream.toParticipantType()

    override val name: String
        get() = publisher.stream.name

    internal val _isMicEnabled: MutableStateFlow<Boolean> = MutableStateFlow(publisher.stream.hasAudio())
    override val isMicEnabled: StateFlow<Boolean>
        get() = _isMicEnabled

    internal val _isCameraEnabled: MutableStateFlow<Boolean> = MutableStateFlow(publisher.stream.hasVideo())
    override val isCameraEnabled: StateFlow<Boolean>
        get() = _isCameraEnabled

    internal val _visible: MutableStateFlow<Boolean>
        get() = MutableStateFlow(false)
    val visible: StateFlow<Boolean>
        get() = _visible

    internal val _audioLevel: MutableStateFlow<Float> = MutableStateFlow(0F)
    val audioLevel: StateFlow<Float>
        get() = _audioLevel

    internal val _isSpeaking: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isTalking: StateFlow<Boolean>
        get() = _isSpeaking

    override val view: View = publisher.view

    suspend fun setup() {

    }

    fun clean(session: Session) {
        publisher.setVideoListener(null)

    }

    override fun onReconnected(subscriber: SubscriberKit) {
        Log.d(TAG, "Subscriber reconnected")
    }

    override fun onDisconnected(subscriber: SubscriberKit) {
        Log.d(TAG, "Subscriber disconnected")
    }

    override fun onAudioDisabled(subscriber: SubscriberKit) {
        Log.d(TAG, "Subscriber audio disabled")
        _isMicEnabled.value = false
    }

    override fun onAudioEnabled(subscriber: SubscriberKit) {
        Log.d(TAG, "Subscriber audio enabled")
        _isMicEnabled.value = true
    }

    override fun onVideoDataReceived(subscriber: SubscriberKit) {
        Log.d(TAG, "Subscriber video data received")
    }

    override fun onVideoDisabled(subscriber: SubscriberKit, reason: String) {
        Log.d(TAG, "Subscriber video disabled")
        _isCameraEnabled.value = false
    }

    override fun onVideoEnabled(subscriber: SubscriberKit, reason: String) {
        Log.d(TAG, "Subscriber video enabled")
        _isCameraEnabled.value = true
    }

    override fun onVideoDisableWarning(subscriber: SubscriberKit) {
        Log.d(TAG, "Subscriber video disable warning")
    }

    override fun onVideoDisableWarningLifted(subscriber: SubscriberKit) {
        Log.d(TAG, "Subscriber video disable warning lifted")
    }
}
