package com.vonage.android.kotlin

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Stable
import com.opentok.android.AudioDeviceManager
import com.opentok.android.Session
import com.vonage.android.kotlin.internal.PublisherFactory
import com.vonage.android.kotlin.internal.VeraAudioDevice
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.signal.SignalPlugin

@Stable
class VonageVideoClient(
    private val context: Context,
    baseAudioDevice: VeraAudioDevice,
    private val signalPlugins: List<SignalPlugin>,
    private val publisherFactory: PublisherFactory = PublisherFactory(),
    // configure log level of the entire SDK
) {

    init {
        AudioDeviceManager.setAudioDevice(baseAudioDevice)
    }

    private var session: Session? = null

    fun debugDump(): String {
        val dump = """
            Android Version: ${Build.VERSION.RELEASE} SDK-${Build.VERSION.SDK_INT}
            Device: ${Build.MANUFACTURER} ${Build.MODEL}
            ===
            Session: ${session?.sessionId}
            Connection: ${session?.connection?.connectionId}
            Connection creation time: ${session?.connection?.creationTime}
            ===
        """.trimIndent()
        return dump
    }

    fun configurePublisher(publisherConfig: PublisherConfig) {
        publisherFactory.init(publisherConfig)
    }

    fun buildPublisher(context: Context): PublisherState =
        publisherFactory.createPublisher(context)

    fun createPreviewPublisher(context: Context, name: String) =
        publisherFactory.createPreviewPublisher(context, name)

    fun destroyPublisher() {
        publisherFactory.destroyPublisher()
    }

    fun initializeSession(apiKey: String, sessionId: String, token: String): CallFacade {
        Log.i(TAG, "apiKey: $apiKey")
        Log.i(TAG, "sessionId: $sessionId")
        Log.i(TAG, "token: $token")

        session = Session.Builder(context, apiKey, sessionId)
            .setSinglePeerConnection(true)
            .build()

        session?.capabilities?.let { capabilities ->
            Log.i(TAG, "Session capabilities:")
            Log.i(TAG, capabilities.toString())
        }

        return Call(
            token = token,
            session = session!!,
            publisherHolder = publisherFactory.publisherHolder!!,
            signalPlugins = signalPlugins,
        )
    }

    private companion object {
        const val TAG: String = "VonageVideoClient"
    }
}
