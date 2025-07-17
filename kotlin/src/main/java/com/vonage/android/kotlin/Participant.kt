package com.vonage.android.kotlin

import android.view.View
import com.opentok.android.Subscriber

interface Participant {
    val id: String
    var name: String
    val isMicEnabled: Boolean
    val isCameraEnabled: Boolean
    val view: View
    fun toggleAudio(): Boolean
    fun toggleVideo(): Boolean
}

internal fun ArrayList<Subscriber>.toParticipants(): List<Participant> =
    this.map { it.toParticipant() }

internal fun Subscriber.toParticipant(): Participant = VeraSubscriber(this)
