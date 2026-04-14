package com.vonage.android.kotlin

import android.content.Context

/**
 * Convenience helper that creates a [VonageSession] via the default factory,
 * connects, and exposes capabilities.
 *
 * For full control, use [VonageSdkFactory] and [VonageSession] directly.
 */
class VonageSessionHelper(
    private val sdkFactory: VonageSdkFactory = VonageSdkFactory.create(),
) {

    private lateinit var session: VonageSession

    fun build(context: Context, apiKey: String, sessionId: String) {
        session = sdkFactory.createSession(context, apiKey, sessionId)
    }

    fun connect(token: String) {
        session.connect(token)
    }
}
