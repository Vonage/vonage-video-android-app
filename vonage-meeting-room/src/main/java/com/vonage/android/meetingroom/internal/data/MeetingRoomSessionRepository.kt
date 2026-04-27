package com.vonage.android.meetingroom.internal.data

internal class MeetingRoomSessionRepository(
    private val apiService: MeetingRoomApiService,
) {

    suspend fun getSession(roomName: String): Result<SessionInfo> =
        runCatching {
            val response = apiService.getSession(roomName)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(
                        SessionInfo(
                            apiKey = it.apiKey,
                            sessionId = it.sessionId,
                            token = it.token,
                            captionsId = it.captionsId,
                        )
                    )
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed getting session"))
            }
        }
}
