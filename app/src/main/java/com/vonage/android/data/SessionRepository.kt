package com.vonage.android.data

import com.vonage.android.data.network.APIService
import com.vonage.android.data.network.GetSessionResponse
import javax.inject.Inject

class SessionRepository @Inject constructor(
    private val apiService: APIService,
) {

    @Suppress("TooGenericExceptionCaught")
    suspend fun getSession(roomName: String): Result<SessionInfo> {
        try {
            val response = apiService.getSession(roomName)
            if (response.isSuccessful) {
                return Result.success(response.body()!!.toSessionInfo())
            }
        } catch (throwable: Throwable) {
            return Result.failure(throwable)
        }
        return Result.failure(Exception("Failed getting session"))
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
