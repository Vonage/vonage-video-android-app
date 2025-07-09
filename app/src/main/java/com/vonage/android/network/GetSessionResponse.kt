package com.vonage.android.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetSessionResponse(
    @Json(name = "apiKey")
    var apiKey: String = "",

    @Json(name = "sessionId")
    var sessionId: String = "",

    @Json(name = "token")
    var token: String = "",
)
