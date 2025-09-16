package com.vonage.android.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {

    @GET("session/{room}")
    suspend fun getSession(@Path("room") room: String): Response<GetSessionResponse>

    @POST("session/{room}/startArchive")
    suspend fun startArchiving(@Path("room") room: String): Response<StartArchivingResponse>

    @POST("session/{room}/{archiveId}/stopArchive")
    suspend fun stopArchiving(
        @Path("room") room: String,
        @Path("archiveId") archiveId: String,
    ): Response<StopArchivingResponse>

    @GET("session/{room}/archives")
    suspend fun getArchives(@Path("room") room: String): Response<GetArchivesResponse>

    @POST("session/{room}/enableCaptions")
    suspend fun enableCaptions(@Path("room") room: String): Response<EnableCaptionsResponse>

    @POST("session/{room}/{captionsId}/disableCaptions")
    suspend fun disableCaptions(
        @Path("room") room: String,
        @Path("captionsId") captionsId: String,
    ): Response<DisableCaptionsResponse>

    @POST("feedback/report")
    suspend fun report(@Body reportDataRequest: ReportDataRequest): Response<ReportResponse>

}
