package com.vonage.android.kotlin

import android.content.Context
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.Publisher
import com.vonage.android.kotlin.model.VeraPublisher

class PublisherFactory {

    fun buildPublisher(context: Context): VeraPublisher {
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
