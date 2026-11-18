package com.vonage.android.vera.api.captions

import com.vonage.android.vera.api.http.SessionKeyRequest
import com.vonage.android.vera.api.http.TrpcResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface CaptionsService {

    @POST("v2/ensureCaptionsEnabled")
    suspend fun ensureCaptionsEnabled(
        @Body body: SessionKeyRequest,
    ): Response<TrpcResponse<EnableCaptionsResponse>>

}

@Serializable
internal data class EnableCaptionsResponse(
    @SerialName("captionsId")
    val captionsId: String? = null,
)
