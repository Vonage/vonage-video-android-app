package com.vonage.android.shared.session

import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class SessionRepository(
    private val apiService: SessionApiService,
) {

    /**
     * Fetches the session credentials for [roomName]. A `401` response fails with
     * [SessionUnauthorizedException] so callers can ask the user to sign in.
     */
    suspend fun getSession(roomName: String): Result<SessionInfo> =
        runCatching {
            val response = apiService.getSession(roomName)
            return when {
                response.isSuccessful -> response.body()?.let {
                    Result.success(
                        SessionInfo(
                            apiKey = it.apiKey,
                            sessionId = it.sessionId,
                            token = it.token,
                            captionsId = it.captionsId,
                        )
                    )
                } ?: Result.failure(Exception("Empty response"))

                response.code() == HTTP_UNAUTHORIZED -> Result.failure(SessionUnauthorizedException())

                else -> Result.failure(Exception("Failed getting session"))
            }
        }
}
