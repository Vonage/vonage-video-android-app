package com.vonage.android.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetSessionResponse(
    @field:Json(name = "apiKey")
    val apiKey: String,

    @field:Json(name = "sessionId")
    val sessionId: String,

    @field:Json(name = "token")
    val token: String,
)
