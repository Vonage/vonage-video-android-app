package com.vonage.android.screen.waiting

import android.content.Context
import android.view.View
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.Publisher
import com.vonage.android.kotlin.Participant
import javax.inject.Inject

class CreatePublisherUseCase @Inject constructor() {

    fun invoke(context: Context): Participant {
        val publisher = Publisher.Builder(context)
            .name("Testing")
            .videoTrack(true)
            .audioTrack(true)
            .build()
            .apply {
                renderer?.setStyle(
                    BaseVideoRenderer.STYLE_VIDEO_SCALE,
                    BaseVideoRenderer.STYLE_VIDEO_FILL
                )
            }
        return object : Participant {
            override val isMicEnabled: Boolean = publisher.publishAudio

            override val isCameraEnabled: Boolean = publisher.publishVideo

            override fun getView(): View = publisher.view

            override fun getName(): String = publisher.stream?.name.orEmpty()
        }
    }
}
