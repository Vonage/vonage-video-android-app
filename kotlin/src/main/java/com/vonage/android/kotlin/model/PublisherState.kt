package com.vonage.android.kotlin.model

import android.util.Log
import android.view.View
import androidx.compose.runtime.Stable
import com.opentok.android.OpentokError
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.opentok.android.Session
import com.opentok.android.Stream
import com.vonage.android.kotlin.ext.movingAverage
import com.vonage.android.kotlin.ext.observeAudioLevel
import com.vonage.android.kotlin.ext.toggle
import com.vonage.android.kotlin.internal.toParticipantType
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class)
@Stable
data class PublisherState(
    val publisherId: String,
    val publisher: Publisher,
) : Participant,
    PublisherKit.VideoListener,
    PublisherKit.PublisherListener,
    PublisherKit.MuteListener {

    override val id: String = publisherId

    private val logTag = "Publisher[$id]"

    override val isPublisher: Boolean = true

    override val creationTime: Long = publisher.stream.creationTime.time

    override val videoSource: VideoSource = publisher.stream.toParticipantType()

    override val name: String = publisher.stream.name

    private val _isMicEnabled: MutableStateFlow<Boolean> = MutableStateFlow(publisher.stream?.hasAudio() ?: false)
    override val isMicEnabled: StateFlow<Boolean> = _isMicEnabled

    private val _isCameraEnabled: MutableStateFlow<Boolean> = MutableStateFlow(publisher.stream?.hasVideo() ?: false)
    override val isCameraEnabled: StateFlow<Boolean> = _isCameraEnabled

    private val _audioLevel: MutableStateFlow<Float> = MutableStateFlow(0F)
    override val audioLevel: StateFlow<Float> = _audioLevel

    private val _isSpeaking: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isTalking: StateFlow<Boolean> = _isSpeaking

    override val view: View = publisher.view

    override fun changeVisibility(visible: Boolean) {
        when (visible) {
            true -> publisher.publishVideo = publisher.stream.hasVideo()
            false -> publisher.publishVideo = false
        }
    }

    fun toggleVideo() {
        publisher.publishVideo = publisher.publishVideo.toggle()
        _isCameraEnabled.update { publisher.publishVideo }
    }

    fun toggleAudio() {
        publisher.publishAudio = publisher.publishAudio.toggle()
        _isMicEnabled.update { publisher.publishAudio }
    }

    suspend fun setup() {
        publisher.setVideoListener(this)
        publisher.setPublisherListener(this)
        publisher.setMuteListener(this)

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

    override fun onVideoDisabled(publisher: PublisherKit, reason: String) {
        Log.d(logTag, "Publisher video disabled - $reason")
        _isCameraEnabled.value = false
    }

    override fun onVideoEnabled(publisher: PublisherKit, reason: String) {
        Log.d(logTag, "Publisher video disabled - $reason")
        _isCameraEnabled.value = false
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
}
