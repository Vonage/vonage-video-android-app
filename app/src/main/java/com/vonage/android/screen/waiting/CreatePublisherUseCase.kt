package com.vonage.android.screen.waiting

import android.content.Context
import android.view.View
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.Publisher
import com.vonage.android.kotlin.Participant
import javax.inject.Inject

class CreatePublisherUseCase @Inject constructor() {

    operator fun invoke(context: Context): Participant {
        // determine if is an Application Context
        val publisher = Publisher.Builder(context)
            .videoTrack(true)
            .audioTrack(true)
            .build()
            .apply {
                renderer?.setStyle(
                    BaseVideoRenderer.STYLE_VIDEO_SCALE,
                    BaseVideoRenderer.STYLE_VIDEO_FILL,
                )
            }
        return VeraPublisher(publisher)
    }
}

class VeraPublisher(
    private val publisher: Publisher,
) : Participant {

    override var name: String = "" // needed? better use optional?

    override var isMicEnabled: Boolean = true
        get() = publisher.publishAudio

    override var isCameraEnabled: Boolean = true
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
