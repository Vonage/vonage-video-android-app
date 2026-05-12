package com.vonage.android.kotlin.sdk

import android.content.Context

/**
 * Abstraction over a Vonage Video session.
 *
 * Provides all session-level operations (connect, publish, subscribe, signal, etc.)
 * without exposing the underlying SDK implementation. Designed to be easily mockable
 * for testing.
 */
interface VonageSession {

    val sessionId: String
    val connectionId: String?
    val connectionCreationTime: Long?
    val capabilities: String?

    fun connect(token: String)
    fun disconnect()
    fun pause()
    fun resume()

    fun publish(publisher: VonagePublisher)
    fun unpublish(publisher: VonagePublisher)
    fun subscribe(context: Context, stream: VonageStream): VonageSubscriber
    fun unsubscribe(subscriber: VonageSubscriber)

    fun sendSignal(type: String, data: String)
    fun forceMuteStream(stream: VonageStream)

    fun setSessionListener(listener: VonageSessionListener?)
    fun setSignalListener(listener: VonageSignalListener?)
    fun setArchiveListener(listener: VonageArchiveListener?)
}

/**
 * Listener for session lifecycle events.
 */
interface VonageSessionListener {
    fun onConnected()
    fun onDisconnected()
    fun onStreamReceived(stream: VonageStream)
    fun onStreamDropped(stream: VonageStream)
    fun onError(error: VonageError)
}

/**
 * Listener for incoming signals (chat, reactions, etc.).
 */
fun interface VonageSignalListener {
    fun onSignalReceived(type: String?, data: String?, connection: VonageConnection?)
}

/**
 * Listener for archiving state changes.
 */
interface VonageArchiveListener {
    fun onArchiveStarted(id: String, name: String?)
    fun onArchiveStopped(id: String)
}
