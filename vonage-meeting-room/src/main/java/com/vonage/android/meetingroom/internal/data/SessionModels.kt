package com.vonage.android.meetingroom.internal.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CreateSessionRequest(
    @SerialName("roomName")
    val roomName: String,
)

@Serializable
internal data class CreateSessionResponse(
    @SerialName("sessionId")
    val sessionId: String,
    @SerialName("sessionKey")
    val sessionKey: String,
    @SerialName("applicationId")
    val applicationId: String,
)

@Serializable
internal data class JoinSessionResponse(
    @SerialName("token")
    val token: String,
)

internal data class SessionInfo(
    val applicationId: String,
    val sessionId: String,
    val token: String,
    val sessionKey: String,
) {
    // Keep the session key and token out of logs.
    override fun toString(): String = "SessionInfo(applicationId=$applicationId, sessionId=$sessionId)"
}
