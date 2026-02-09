package com.vonage.android.captions.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.inject.Inject

class CaptionsRepository @Inject constructor(
    private val apiService: CaptionsApi,
) {

    suspend fun enableCaptions(roomName: String): Result<String> =
        runCatching {
            val response = apiService.enableCaptions(roomName)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.captionsId)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed enabling captions"))
            }
        }
}

@JsonClass(generateAdapter = true)
data class EnableCaptionsResponse(
    @field:Json(name = "captionsId")
    val captionsId: String,
)
