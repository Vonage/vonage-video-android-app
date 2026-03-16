package com.vonage.android.data.network

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
