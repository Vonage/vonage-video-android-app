package com.vonage.android.kotlin

import android.view.View
import com.opentok.android.Subscriber

class VeraSubscriber(
    private val subscriber: Subscriber,
) : Participant {

    override var name: String = subscriber.stream.name // needed? better use optional?

    override val isMicEnabled: Boolean = subscriber.stream.hasAudio()

    override val isCameraEnabled: Boolean = subscriber.stream.hasVideo()

    override val view: View = subscriber.view

    override fun toggleAudio(): Boolean {
        return false
    }

    override fun toggleVideo(): Boolean {
        return true
    }
}
