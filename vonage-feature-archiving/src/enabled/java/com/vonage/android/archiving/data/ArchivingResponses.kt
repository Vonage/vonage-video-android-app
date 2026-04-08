package com.vonage.android.archiving.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartArchivingResponse(
    @SerialName("archiveId")
    val archiveId: String,
)

@Serializable
data class StopArchivingResponse(
    @SerialName("archiveId")
    val archiveId: String,
)

@Serializable
data class GetArchivesResponse(
    @SerialName("archives")
    val archives: List<ServerArchive>
)

@Serializable
data class ServerArchive(
    @SerialName("id")
    val id: String,
    @SerialName("duration")
    val duration: Int,
    @SerialName("name")
    val name: String,
    @SerialName("url")
    val url: String? = null,
    @SerialName("size")
    val size: Int,
    @SerialName("status")
    val status: String,
    @SerialName("createdAt")
    val createdAt: Long,
)
