package com.vonage.android.shared.session

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionRequest(
    @SerialName("roomName")
    val roomName: String,
)

@Serializable
data class CreateSessionResponse(
    @SerialName("sessionId")
    val sessionId: String,
    @SerialName("sessionKey")
    val sessionKey: String,
    @SerialName("applicationId")
    val applicationId: String,
)

@Serializable
data class JoinSessionResponse(
    @SerialName("token")
    val token: String,
)

data class SessionInfo(
    val applicationId: String,
    val sessionId: String,
    val token: String,
    val sessionKey: String,
) {
    // Keep the session key and token out of logs.
    override fun toString(): String = "SessionInfo(applicationId=$applicationId, sessionId=$sessionId)"
}

/** The backend rejected the session request with `401 Unauthorized`: the user must sign in. */
class SessionUnauthorizedException : Exception("Authentication required to get the session")
