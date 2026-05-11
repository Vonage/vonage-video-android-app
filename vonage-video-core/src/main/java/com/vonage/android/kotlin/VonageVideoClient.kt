package com.vonage.android.kotlin

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.internal.PublisherFactory
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.PreviewPublisherState
import com.vonage.android.kotlin.model.PublisherConfig
import com.vonage.android.kotlin.model.PublisherState
import com.vonage.android.kotlin.sdk.VonageSdkFactory
import com.vonage.android.kotlin.sdk.VonageSession
import com.vonage.android.kotlin.signal.SignalPlugin
import com.vonage.logger.vonageLogger

/**
 * Main entry point for Vonage Video SDK functionality.
 *
 * This client manages the video session lifecycle, publisher configuration, and signal plugins.
 * It provides a high-level API for creating video calls with customizable signal handling.
 *
 * @param context The Android application context
 * @param sdkFactory Factory for creating SDK objects (sessions, publishers, subscribers)
 * @param signalPlugins List of signal plugins for handling custom signaling (chat, reactions, etc.)
 * @param publisherFactory Factory for creating publisher instances (injectable for testing)
 */
@Stable
class VonageVideoClient(
    private val context: Context,
    private val sdkFactory: VonageSdkFactory,
    private val signalPlugins: List<SignalPlugin>,
    private val publisherFactory: PublisherFactory,
) {

    private var session: VonageSession? = null

    fun debugDump(): String {
        val dump = """
            Android Version: ${Build.VERSION.RELEASE} SDK-${Build.VERSION.SDK_INT}
            Device: ${Build.MANUFACTURER} ${Build.MODEL}
            ===
            Session: ${session?.sessionId}
            Connection: ${session?.connectionId}
            Connection creation time: ${session?.connectionCreationTime}
            ===
        """.trimIndent()
        return dump
    }

    fun configurePublisher(publisherConfig: PublisherConfig) {
        publisherFactory.init(publisherConfig)
    }

    fun buildPublisher(context: Context): PublisherState =
        publisherFactory.createPublisherState(context)

    fun createPreviewPublisher(context: Context): PreviewPublisherState =
        publisherFactory.createPreviewPublisher(context)

    /**
     * Creates a standalone preview publisher for the effects sheet that is never published
     * to any session. The real session publisher is unaffected.
     *
     * Call [destroyIsolatedPreviewPublisher] when the sheet is dismissed.
     */
    fun createIsolatedPreviewPublisher(context: Context): PreviewPublisherState =
        publisherFactory.createIsolatedPreviewPublisher(context)

    fun destroyPublisher() {
        publisherFactory.destroyPublisher()
    }

    /** Releases the isolated preview publisher created by [createIsolatedPreviewPublisher]. */
    fun destroyIsolatedPreviewPublisher() =
        publisherFactory.destroyIsolatedPreviewPublisher()

    /**
     * Creates a new session and returns a [CallFacade] ready for connection.
     *
     * Instantiates the underlying [VonageSession] via the SDK factory and wraps it in a [Call]
     * object together with the publisher factory and any registered signal plugins.
     *
     * **Note:** This method does NOT connect to the session. Call [CallFacade.connect] on the
     * returned facade to actually establish the WebRTC connection and start publishing.
     *
     * Typical call chain (orchestrated by `MeetingRoomScreenViewModel`):
     * ```
     * SessionRepository.getSession() → VonageVideoClient.initializeSession() → CallFacade.connect()
     * ```
     *
     * @param apiKey   Vonage project API key obtained from the backend session endpoint.
     * @param sessionId Vonage session ID obtained from the backend session endpoint.
     * @param token    Short-lived token authorising this participant to join the session.
     * @return A [CallFacade] combining session, publisher, chat, emoji, and screen-share facades.
     */
    fun initializeSession(apiKey: String, sessionId: String, token: String): CallFacade {
        vonageLogger.d(TAG, "apiKey: $apiKey")
        vonageLogger.d(TAG, "sessionId: $sessionId")
        vonageLogger.d(TAG, "token: $token")

        val vonageSession = sdkFactory.createSession(context, apiKey, sessionId)
        session = vonageSession

        vonageSession.capabilities?.let { capabilities ->
            vonageLogger.i(TAG, "Session capabilities:")
            vonageLogger.i(TAG, capabilities)
        }

        return Call(
            token = token,
            session = vonageSession,
            publisherFactory = publisherFactory,
            signalPlugins = signalPlugins,
        )
    }

    private companion object {
        const val TAG: String = "VonageVideoClient"
    }
}
