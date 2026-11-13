package com.vonage.android.vera.api.session

import com.vonage.android.vera.api.VeraApiException
import com.vonage.android.vera.api.http.SessionKeyRequest
import com.vonage.android.vera.api.http.execute
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

interface SessionApi {

    /**
     * Resolves the credentials for [roomName] with the v2 two-step flow:
     * `createSession` (reuses the room's live session) followed by `joinSession` (issues the token).
     *
     * A `401` response from either call fails with [SessionUnauthorizedException] so callers
     * can ask the user to sign in.
     */
    suspend fun getSession(roomName: String): Result<SessionInfo>
}

internal class DefaultSessionApi(
    private val service: SessionService,
) : SessionApi {

    override suspend fun getSession(roomName: String): Result<SessionInfo> {
        val session = execute(CREATE_FAILED, emptyMessage = CREATE_FAILED) {
            service.createSession(CreateSessionRequest(roomName))
        }.map { it.result.data }.mapUnauthorized().getOrElse { return Result.failure(it) }

        return execute(JOIN_FAILED, emptyMessage = JOIN_FAILED) {
            service.joinSession(SessionKeyRequest(session.sessionKey))
        }.map { join ->
            SessionInfo(
                applicationId = session.applicationId,
                sessionId = session.sessionId,
                token = join.result.data.token,
                sessionKey = session.sessionKey,
            )
        }.mapUnauthorized()
    }

    private fun <T> Result<T>.mapUnauthorized(): Result<T> =
        recoverCatching { error ->
            throw if (error is VeraApiException && error.httpCode == HTTP_UNAUTHORIZED) {
                SessionUnauthorizedException()
            } else {
                error
            }
        }

    private companion object {
        const val CREATE_FAILED = "Failed creating session"
        const val JOIN_FAILED = "Failed joining session"
    }
}
