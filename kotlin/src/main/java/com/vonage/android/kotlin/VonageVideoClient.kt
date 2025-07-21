package com.vonage.android.kotlin

import android.content.Context
import android.util.Log
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.Publisher
import com.opentok.android.Session
import com.opentok.android.Session.SessionOptions
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.VeraPublisher
import com.vonage.android.kotlin.model.toParticipant

class VonageVideoClient(
    private val context: Context,
//    lifecycle: Lifecycle,
) {

    private var session: Session? = null
    private var publisherConfig: PublisherConfig? = null
    private var publisherHolder: VeraPublisherHolder? = null

    init {
//        lifecycle.addObserver(object : DefaultLifecycleObserver {
//            override fun onPause(owner: LifecycleOwner) {
//                super.onPause(owner)
//                session?.onPause()
//            }
//
//            override fun onResume(owner: LifecycleOwner) {
//                super.onResume(owner)
//                session?.onResume()
//            }
//        })
    }

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
        val participant = publisher.toParticipant(resolvedName) as VeraPublisher
        this.publisherHolder = VeraPublisherHolder(
            participant = participant,
            publisher = publisher,
        )
        return participant
    }

    fun destroyPublisher() {
        publisherHolder?.publisher?.destroy()
        publisherHolder?.publisher?.onStop()
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
