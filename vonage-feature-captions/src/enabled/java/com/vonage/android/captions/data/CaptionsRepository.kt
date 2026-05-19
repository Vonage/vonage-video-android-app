package com.vonage.android.captions.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class CaptionsRepository(
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

@Serializable
data class EnableCaptionsResponse(
    @SerialName("captionsId")
    val captionsId: String,
)
