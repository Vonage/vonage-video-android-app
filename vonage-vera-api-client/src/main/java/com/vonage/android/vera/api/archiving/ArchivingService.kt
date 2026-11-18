package com.vonage.android.vera.api.archiving

import com.vonage.android.vera.api.http.SessionKeyRequest
import com.vonage.android.vera.api.http.TrpcResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface ArchivingService {

    @POST("v2/startArchive")
    suspend fun startArchiving(@Body body: StartArchiveRequest): Response<TrpcResponse<ArchiveOperationResponse>>

    @POST("v2/stopArchive")
    suspend fun stopArchiving(@Body body: StopArchiveRequest): Response<TrpcResponse<ArchiveOperationResponse>>

    @POST("v2/searchArchives")
    suspend fun searchArchives(@Body body: SessionKeyRequest): Response<TrpcResponse<SearchArchivesResponse>>

}
