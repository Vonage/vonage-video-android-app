package com.vonage.android.kotlin

import android.content.Context
import android.util.Log
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.Publisher
import com.opentok.android.Session
import com.opentok.android.Session.SessionOptions
import com.vonage.android.kotlin.internal.VeraPublisherHolder
import com.vonage.android.kotlin.internal.toParticipant
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.VeraPublisher

class VonageVideoClient(
    private val context: Context,
    // configure log level of the entire SDK
) {

    private var session: Session? = null
    private var publisherConfig: PublisherConfig? = null
    private var publisherHolder: VeraPublisherHolder? = null

    fun configurePublisher(publisherConfig: PublisherConfig) {
        this.publisherConfig = publisherConfig
    }

    fun buildPublisher(): VeraPublisher {
        val resolvedName = publisherConfig?.name ?: ""
        val publisher = Publisher.Builder(context)
            .name(resolvedName)
            .videoTrack(true)
            .audioTrack(true)
            .build()
            .apply {
                renderer?.setStyle(
                    BaseVideoRenderer.STYLE_VIDEO_SCALE,
                    BaseVideoRenderer.STYLE_VIDEO_FIT,
                )
                publishVideo = publisherConfig?.publishVideo ?: true
                publishAudio = publisherConfig?.publishAudio ?: true
            }
        val participant = publisher.toParticipant(resolvedName)
        this.publisherHolder = VeraPublisherHolder(
            participant = participant,
            publisher = publisher,
        )
        return participant
    }

    fun destroyPublisher() {
        publisherHolder?.publisher?.let {
            it.destroy()
            it.onStop()
        }
        publisherHolder = null
        Log.i(TAG, "Destroy publisher")
    }

    fun initializeSession(apiKey: String, sessionId: String, token: String): Call {
        Log.i(TAG, "apiKey: $apiKey")
        Log.i(TAG, "sessionId: $sessionId")
        Log.i(TAG, "token: $token")

        session = Session.Builder(context, apiKey, sessionId)
            .sessionOptions(
                object : SessionOptions() {
                    override fun useTextureViews(): Boolean = true
                }
            ).build()

        return Call(
            context = context,
            token = token,
            session = session!!,
            publisherHolder = publisherHolder!!,
        )
    }

    private companion object {
        const val TAG: String = "VonageVideoClient"
    }
}
