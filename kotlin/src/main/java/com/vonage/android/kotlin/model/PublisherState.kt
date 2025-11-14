package com.vonage.android.kotlin.model

import android.util.Log
import android.view.View
import androidx.compose.runtime.Stable
import com.opentok.android.OpentokError
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.opentok.android.Session
import com.opentok.android.Stream
import com.vonage.android.kotlin.Call.Companion.PUBLISHER_ID
import com.vonage.android.kotlin.ext.observeAudioLevel
import com.vonage.android.kotlin.internal.toParticipantType
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@OptIn(FlowPreview::class)
@Stable
data class PublisherState(
    val publisher: Publisher,
) : Participant,
    PublisherKit.VideoListener,
    PublisherKit.PublisherListener,
    PublisherKit.MuteListener {

    private val logTag = "Publisher[$id]"

    override val id: String
        get() = PUBLISHER_ID

    override val isPublisher: Boolean
        get() = true

    override val creationTime: Long
        get() = publisher.stream.creationTime.time

    override val videoSource: VideoSource
        get() = publisher.stream.toParticipantType()

    override val name: String
        get() = publisher.stream.name

    internal val _isMicEnabled: MutableStateFlow<Boolean> = MutableStateFlow(publisher.stream?.hasAudio() ?: false)
    override val isMicEnabled: StateFlow<Boolean>
        get() = _isMicEnabled

    internal val _isCameraEnabled: MutableStateFlow<Boolean> = MutableStateFlow(publisher.stream?.hasVideo() ?: false)
    override val isCameraEnabled: StateFlow<Boolean>
        get() = _isCameraEnabled

    private val _visible: MutableStateFlow<Boolean>
        get() = MutableStateFlow(false)
    val visible: StateFlow<Boolean>
        get() = _visible

    private val _audioLevel: MutableStateFlow<Float> = MutableStateFlow(0F)
    override val audioLevel: StateFlow<Float>
        get() = _audioLevel

    private val _isSpeaking: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isTalking: StateFlow<Boolean>
        get() = _isSpeaking

    override val view: View = publisher.view

    override fun changeVisibility(visible: Boolean) {

    }

    suspend fun setup() {
        publisher.setVideoListener(this)
        publisher.setPublisherListener(this)
        publisher.setMuteListener(this)

        publisher.observeAudioLevel()
            .filter { publisher.publishAudio }
            .distinctUntilChanged()
            .debounce(PUBLISHER_AUDIO_LEVEL_DEBOUNCE)
            .collect { audioLevel ->
                _audioLevel.value = audioLevel
            }
    }

    override fun clean(session: Session) {
        publisher.setVideoListener(null)

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

private const val PUBLISHER_AUDIO_LEVEL_DEBOUNCE = 60L
