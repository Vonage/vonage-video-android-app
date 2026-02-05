package com.vonage.android.captions.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EnableCaptionsResponse(
    @field:Json(name = "captionsId")
    val captionsId: String,
)

@JsonClass(generateAdapter = true)
data class DisableCaptionsResponse(
    @field:Json(name = "disableResponse")
    val disableResponse: String,
)
