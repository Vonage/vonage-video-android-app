package com.vonage.android.meetingroom.internal.data

internal class MeetingRoomSessionRepository(
    private val apiService: MeetingRoomApiService,
) {

    suspend fun getSession(roomName: String): Result<GetSessionResponse> = runCatching {
        val response = apiService.getSession(roomName)
        check(response.isSuccessful) { "Failed getting session" }
        response.body() ?: error("Empty response")
    }
}
