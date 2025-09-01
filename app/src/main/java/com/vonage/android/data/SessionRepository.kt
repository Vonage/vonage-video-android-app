package com.vonage.android.data

import com.vonage.android.data.network.APIService
import com.vonage.android.data.network.GetSessionResponse
import javax.inject.Inject

class SessionRepository @Inject constructor(
    private val apiService: APIService,
) {

    suspend fun getSession(roomName: String): Result<SessionInfo> =
        runCatching {
            val response = apiService.getSession(roomName)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toSessionInfo())
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed getting session"))
            }
        }

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

    suspend fun disableCaptions(roomName: String, captionsId: String): Result<String> =
        runCatching {
            val response = apiService.disableCaptions(roomName, captionsId)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.disableResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed enabling captions"))
            }
        }
}

fun GetSessionResponse.toSessionInfo() =
    SessionInfo(
        apiKey = this.apiKey,
        sessionId = this.sessionId,
        token = this.token,
        captionsId = this.captionsId,
    )

data class SessionInfo(
    val apiKey: String,
    val sessionId: String,
    val token: String,
    val captionsId: String?,
)
