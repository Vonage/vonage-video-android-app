package com.vonage.android.archiving.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ArchivingApi {

    @POST("session/{room}/startArchive")
    suspend fun startArchiving(@Path("room") room: String): Response<StartArchivingResponse>

    @POST("session/{room}/{archiveId}/stopArchive")
    suspend fun stopArchiving(
        @Path("room") room: String,
        @Path("archiveId") archiveId: String,
    ): Response<StopArchivingResponse>

    @GET("session/{room}/archives")
    suspend fun getArchives(@Path("room") room: String): Response<GetArchivesResponse>

}