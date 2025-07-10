package com.vonage.android.data

import com.vonage.android.data.network.APIService
import com.vonage.android.data.network.GetSessionResponse
import javax.inject.Inject

class SessionRepository @Inject constructor(
    private val apiService: APIService,
) {

    @Suppress("TooGenericExceptionCaught")
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
}

fun GetSessionResponse.toSessionInfo() =
    SessionInfo(
        apiKey = this.apiKey,
        sessionId = this.sessionId,
        token = this.token,
    )

data class SessionInfo(
    val apiKey: String,
    val sessionId: String,
    val token: String,
)
