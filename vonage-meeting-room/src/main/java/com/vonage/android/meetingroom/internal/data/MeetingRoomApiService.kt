package com.vonage.android.meetingroom.internal.data

import com.vonage.android.shared.network.SessionKeyRequest
import com.vonage.android.shared.network.TrpcResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface MeetingRoomApiService {

    /** Creates the session for the room, or returns the existing one when the room is already live. */
    @POST("v2/createSession")
    suspend fun createSession(@Body body: CreateSessionRequest): Response<TrpcResponse<CreateSessionResponse>>

    /** Issues a client token for the session identified by the session key. */
    @POST("v2/joinSession")
    suspend fun joinSession(@Body body: SessionKeyRequest): Response<TrpcResponse<JoinSessionResponse>>
}
