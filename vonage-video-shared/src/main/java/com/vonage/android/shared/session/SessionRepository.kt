package com.vonage.android.shared.session

import com.vonage.android.shared.network.SessionKeyRequest
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class SessionRepository(
    private val apiService: SessionApiService,
) {

    /**
     * Resolves the credentials for [roomName] with the v2 two-step flow:
     * `createSession` (reuses the room's live session) followed by `joinSession` (issues the token).
     *
     * A `401` response from either call fails with [SessionUnauthorizedException] so callers
     * can ask the user to sign in.
     */
    suspend fun getSession(roomName: String): Result<SessionInfo> =
        runCatching {
            val createResponse = apiService.createSession(CreateSessionRequest(roomName))
            if (createResponse.code() == HTTP_UNAUTHORIZED) {
                return Result.failure(SessionUnauthorizedException())
            }
            val session = createResponse.body()?.result?.data
            if (!createResponse.isSuccessful || session == null) {
                return Result.failure(Exception("Failed creating session"))
            }

            val joinResponse = apiService.joinSession(SessionKeyRequest(session.sessionKey))
            if (joinResponse.code() == HTTP_UNAUTHORIZED) {
                return Result.failure(SessionUnauthorizedException())
            }
            val join = joinResponse.body()?.result?.data
            if (!joinResponse.isSuccessful || join == null) {
                return Result.failure(Exception("Failed joining session"))
            }

            SessionInfo(
                applicationId = session.applicationId,
                sessionId = session.sessionId,
                token = join.token,
                sessionKey = session.sessionKey,
            )
        }
}
