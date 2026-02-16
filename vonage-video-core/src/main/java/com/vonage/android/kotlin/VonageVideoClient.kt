package com.vonage.android.kotlin

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Stable
import com.opentok.android.AudioDeviceManager
import com.opentok.android.BaseAudioDevice
import com.opentok.android.Session
import com.vonage.android.kotlin.internal.PublisherFactory
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PreviewPublisherState
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.signal.SignalPlugin
import com.vonage.logger.vonageLogger

/**
 * Main entry point for Vonage Video SDK functionality.
 *
 * This client manages the video session lifecycle, publisher configuration, and signal plugins.
 * It provides a high-level API for creating video calls with customizable audio devices and
 * signal handling (chat, reactions, etc.).
 *
 * @param context The Android application context
 * @param baseAudioDevice Custom audio device implementation for call audio routing
 * @param signalPlugins List of signal plugins for handling custom signaling (chat, reactions, etc.)
 * @param publisherFactory Factory for creating publisher instances (injectable for testing)
 */
@Stable
class VonageVideoClient(
    private val context: Context,
    baseAudioDevice: BaseAudioDevice,
    private val signalPlugins: List<SignalPlugin>,
    private val publisherFactory: PublisherFactory = PublisherFactory(),
) {

    init {
        AudioDeviceManager.setAudioDevice(baseAudioDevice)
    }

    private var session: Session? = null

    /**
     * Generates a debug information dump containing device and session details.
     *
     * @return String containing Android version, device model, session ID, and connection information
     */
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

    /**
     * Configures the publisher with initial settings.
     *
     * Must be called before creating a publisher to set camera, audio, and blur settings.
     *
     * @param publisherConfig Configuration including name, video/audio state, blur level, and camera
     */
    fun configurePublisher(publisherConfig: PublisherConfig) {
        publisherFactory.init(publisherConfig)
    }

    /**
     * Creates a publisher instance for the current user.
     *
     * @param context Android context for publisher creation
     * @return PublisherState wrapping the created publisher with reactive state flows
     */
    fun buildPublisher(context: Context): PublisherState =
        publisherFactory.createPublisher(context)

    /**
     * Creates a preview-only publisher for showing camera preview before joining a call.
     *
     * @param context Android context for publisher creation
     * @param name Display name for the preview publisher
     * @return PreviewPublisherState for camera preview
     */
    fun createPreviewPublisher(context: Context, name: String): PreviewPublisherState =
        publisherFactory.createPreviewPublisher(context, name)

    /**
     * Destroys the current publisher and releases associated resources.
     */
    fun destroyPublisher() {
        publisherFactory.destroyPublisher()
    }

    /**
     * Initializes a new video session and returns a CallFacade for managing the call.
     *
     * Creates a Vonage session with the provided credentials and configures it for single
     * peer connection mode. The returned CallFacade provides access to all call features
     * including participant management, signaling, and screen sharing.
     *
     * @param apiKey Vonage API key for the application
     * @param sessionId Unique session identifier
     * @param token Authentication token for joining the session
     * @return CallFacade interface for managing the video call
     */
    fun initializeSession(apiKey: String, sessionId: String, token: String): CallFacade {
        vonageLogger.i(TAG, "apiKey: $apiKey")
        vonageLogger.i(TAG, "sessionId: $sessionId")
        vonageLogger.i(TAG, "token: $token")

        session = Session.Builder(context, apiKey, sessionId)
            .setSinglePeerConnection(true)
            .setSessionMigration(true)
            .build()

        session?.capabilities?.let { capabilities ->
            vonageLogger.i(TAG, "Session capabilities:")
            vonageLogger.i(TAG, capabilities.toString())
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
