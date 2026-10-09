package com.vonage.android.archiving.data

import com.vonage.android.shared.network.SessionKeyRequest
import com.vonage.android.shared.network.TrpcResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ArchivingApi {

    @POST("v2/startArchive")
    suspend fun startArchiving(@Body body: SessionKeyRequest): Response<TrpcResponse<ArchiveOperationResponse>>

    @POST("v2/stopArchive")
    suspend fun stopArchiving(@Body body: StopArchiveRequest): Response<TrpcResponse<ArchiveOperationResponse>>

    @POST("v2/searchArchives")
    suspend fun searchArchives(@Body body: SessionKeyRequest): Response<TrpcResponse<SearchArchivesResponse>>

}
