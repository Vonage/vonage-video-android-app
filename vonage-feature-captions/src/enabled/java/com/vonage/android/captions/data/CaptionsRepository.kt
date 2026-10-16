package com.vonage.android.captions.data

import com.vonage.android.shared.network.SessionKeyRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class CaptionsRepository(
    private val apiService: CaptionsApi,
) {

    /**
     * Makes sure live captions are running for the session.
     *
     * @return The new captions id, or `null` when captions were already running for the session
     * (e.g. enabled by another participant). Both cases are a success.
     */
    suspend fun enableCaptions(sessionKey: String): Result<String?> =
        runCatching {
            val response = apiService.ensureCaptionsEnabled(SessionKeyRequest(sessionKey))
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.result.data.captionsId)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed enabling captions"))
            }
        }
}

@Serializable
data class EnableCaptionsResponse(
    @SerialName("captionsId")
    val captionsId: String? = null,
)
