package com.vonage.android.kotlin

/**
 * Represents the type of video stream.
 */
enum class VonageVideoType {
    CAMERA,
    SCREEN,
    CUSTOM,
}

/**
 * Represents a video stream in a Vonage Video session.
 *
 * This is a snapshot of the stream state at creation time.
 *
 * @property streamId Unique identifier for the stream
 * @property name Display name of the stream
 * @property connection Connection associated with this stream
 * @property creationTime Timestamp (epoch millis) when the stream was created
 * @property videoType Type of video source (camera, screen, custom)
 * @property hasVideo Whether the stream has video enabled
 * @property hasAudio Whether the stream has audio enabled
 */
data class VonageStream(
    val streamId: String,
    val name: String,
    val connection: VonageConnection,
    val creationTime: Long,
    val videoType: VonageVideoType,
    val hasVideo: Boolean,
    val hasAudio: Boolean,
)
