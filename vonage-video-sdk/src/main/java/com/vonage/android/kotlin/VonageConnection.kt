package com.vonage.android.kotlin

/**
 * Represents a connection to a Vonage Video session.
 *
 * @property connectionId Unique identifier for the connection
 * @property creationTime Timestamp (epoch millis) when the connection was created
 */
data class VonageConnection(
    val connectionId: String,
    val creationTime: Long = 0,
)
