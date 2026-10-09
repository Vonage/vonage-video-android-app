package com.vonage.android.shared.session

import com.vonage.android.shared.network.SessionKeyRequest
import com.vonage.android.shared.network.TrpcResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Backend v2 endpoints returning the Vonage Video session credentials for a room.
 *
 * Each caller creates it from its own Retrofit instance (`retrofit.create(SessionApiService::class.java)`)
 * so its own interceptors (e.g. the `Authorization` header) apply.
 */
interface SessionApiService {

    /** Creates the session for the room, or returns the existing one when the room is already live. */
    @POST("v2/createSession")
    suspend fun createSession(@Body body: CreateSessionRequest): Response<TrpcResponse<CreateSessionResponse>>

    /** Issues a client token for the session identified by the session key. */
    @POST("v2/joinSession")
    suspend fun joinSession(@Body body: SessionKeyRequest): Response<TrpcResponse<JoinSessionResponse>>
}
