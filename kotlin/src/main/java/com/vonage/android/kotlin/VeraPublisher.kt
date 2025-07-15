package com.vonage.android.kotlin

import android.view.View
import com.opentok.android.Publisher

class VeraPublisher(
    private val publisher: Publisher,
) : Participant {

    override var name: String = "" // needed? better use optional?

    override val isMicEnabled: Boolean
        get() = publisher.publishAudio

    override val isCameraEnabled: Boolean
        get() = publisher.publishVideo

    override val view: View = publisher.view

    override fun toggleAudio(): Boolean {
        val enableAudio = !publisher.publishAudio
        publisher.publishAudio = enableAudio
        return publisher.publishAudio
    }

    override fun toggleVideo(): Boolean {
        val enableVideo = !publisher.publishVideo
        publisher.publishVideo = enableVideo
        return publisher.publishVideo
    }
}
