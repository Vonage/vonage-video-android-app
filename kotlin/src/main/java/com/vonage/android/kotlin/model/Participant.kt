package com.vonage.android.kotlin.model

import android.view.View
import com.opentok.android.Publisher
import com.opentok.android.Subscriber

interface Participant {
    val id: String
    var name: String
    val isMicEnabled: Boolean
    val isCameraEnabled: Boolean
    val view: View
}

internal fun Subscriber.toParticipant(): Participant = VeraSubscriber(
    id = stream.streamId,
    name = stream.name,
    isMicEnabled = stream.hasAudio(),
    isCameraEnabled = stream.hasVideo(),
    view = view,
)

internal fun Publisher.toParticipant(
    name: String? = null,
): Participant = VeraPublisher(
    id = stream?.streamId ?: "publisher",
    name = stream?.name ?: name.orEmpty(),
    isMicEnabled = publishAudio,
    isCameraEnabled = publishVideo,
    view = view,
)
