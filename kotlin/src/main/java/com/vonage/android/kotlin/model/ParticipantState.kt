package com.vonage.android.kotlin.model

import android.view.View
import androidx.compose.runtime.Stable
import com.opentok.android.Session
import com.opentok.android.Subscriber
import com.vonage.android.kotlin.ext.name
import com.vonage.android.kotlin.internal.toParticipantType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Stable
data class ParticipantState(
    private val subscriber: Subscriber,
): Participant {

    override val id: String
        get() = subscriber.stream.streamId

    override val videoSource: VideoSource
        get() = subscriber.toParticipantType()

    override val name: String
        get() = subscriber.name()

    internal val _isMicEnabled: MutableStateFlow<Boolean> = MutableStateFlow(subscriber.stream.hasAudio())
    override val isMicEnabled: StateFlow<Boolean>
        get() = _isMicEnabled

    internal val _isCameraEnabled: MutableStateFlow<Boolean> = MutableStateFlow(subscriber.stream.hasVideo())
    override val isCameraEnabled: StateFlow<Boolean>
        get() = _isCameraEnabled

    var subscribeToVideo: Boolean
        get() = subscriber.subscribeToVideo
        set(value) { subscriber.subscribeToVideo = value }

    internal val _isSpeaking: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean>
        get() = _isSpeaking

    internal val _view = MutableStateFlow<View>(subscriber.view)
    override val view: StateFlow<View>
        get() = _view

    fun clean(session: Session) {
        subscriber.setVideoListener(null)
        subscriber.setStreamListener(null)
        subscriber.setAudioLevelListener(null)
        session.unsubscribe(subscriber)
    }
}
