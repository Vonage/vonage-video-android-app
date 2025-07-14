package com.vonage.android.screen.waiting

import android.content.Context
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.Publisher
import com.vonage.android.kotlin.Participant
import com.vonage.android.kotlin.VeraPublisher
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CreatePublisherUseCase @Inject constructor(
    @param:ApplicationContext val context: Context,
) {
    operator fun invoke(): Participant {
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
