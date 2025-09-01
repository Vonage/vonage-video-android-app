package com.vonage.android.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StartArchivingResponse(
    @field:Json(name = "archiveId")
    val archiveId: String,
)

@JsonClass(generateAdapter = true)
data class StopArchivingResponse(
    @field:Json(name = "archiveId")
    val archiveId: String,
)

@JsonClass(generateAdapter = true)
data class GetArchivesResponse(
    @field:Json(name = "archives")
    val archives: List<ServerArchive>
)

@JsonClass(generateAdapter = true)
data class ServerArchive(
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "url")
    val url: String?,
    @field:Json(name = "status")
    val status: String,
    @field:Json(name = "createdAt")
    val createdAt: Long,
)
