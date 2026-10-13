package com.vonage.android.shared.session

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetSessionResponse(
    @SerialName("apiKey")
    val apiKey: String,
    @SerialName("sessionId")
    val sessionId: String,
    @SerialName("token")
    val token: String,
    @SerialName("captionsId")
    val captionsId: String? = null,
)

data class SessionInfo(
    val apiKey: String,
    val sessionId: String,
    val token: String,
    val captionsId: String?,
)

/** The backend rejected the session request with `401 Unauthorized`: the user must sign in. */
class SessionUnauthorizedException : Exception("Authentication required to get the session")
