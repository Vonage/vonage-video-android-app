package com.vonage.android.captions.data

import com.vonage.android.shared.network.SessionKeyRequest
import com.vonage.android.shared.network.TrpcResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CaptionsApi {

    @POST("v2/ensureCaptionsEnabled")
    suspend fun ensureCaptionsEnabled(
        @Body body: SessionKeyRequest,
    ): Response<TrpcResponse<EnableCaptionsResponse>>

}
