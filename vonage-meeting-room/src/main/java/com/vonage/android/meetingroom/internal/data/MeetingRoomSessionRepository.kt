package com.vonage.android.meetingroom.internal.data

import com.vonage.android.shared.network.SessionKeyRequest

internal class MeetingRoomSessionRepository(
    private val apiService: MeetingRoomApiService,
) {

    /**
     * Resolves the credentials for [roomName] with the v2 two-step flow:
     * `createSession` (reuses the room's live session) followed by `joinSession` (issues the token).
     */
    suspend fun getSession(roomName: String): Result<SessionInfo> =
        runCatching {
            val createResponse = apiService.createSession(CreateSessionRequest(roomName))
            val session = createResponse.body()?.result?.data
            if (!createResponse.isSuccessful || session == null) {
                return Result.failure(Exception("Failed creating session"))
            }

            val joinResponse = apiService.joinSession(SessionKeyRequest(session.sessionKey))
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
