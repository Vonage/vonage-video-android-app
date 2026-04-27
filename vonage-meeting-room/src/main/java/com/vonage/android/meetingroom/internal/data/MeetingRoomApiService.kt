package com.vonage.android.meetingroom.internal.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

internal interface MeetingRoomApiService {

    @GET("session/{room}")
    suspend fun getSession(@Path("room") room: String): Response<GetSessionResponse>
}
