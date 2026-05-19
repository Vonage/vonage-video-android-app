package com.vonage.android.meetingroom.internal.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GetSessionResponse(
    @SerialName("apiKey")
    val apiKey: String,
    @SerialName("sessionId")
    val sessionId: String,
    @SerialName("token")
    val token: String,
    @SerialName("captionsId")
    val captionsId: String? = null,
)

internal data class SessionInfo(
    val apiKey: String,
    val sessionId: String,
    val token: String,
    val captionsId: String?,
)
