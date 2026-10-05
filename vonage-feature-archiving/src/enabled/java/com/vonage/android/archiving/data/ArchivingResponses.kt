package com.vonage.android.archiving.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Body for `POST /v2/stopArchive`.
 *
 * [archiveId] is optional: when omitted the backend stops the archive it has stored for the session.
 * The default `null` is not encoded (kotlinx `encodeDefaults = false`), so the key is left out of the JSON.
 */
@Serializable
data class StopArchiveRequest(
    @SerialName("sessionKey")
    val sessionKey: String,
    @SerialName("archiveId")
    val archiveId: String? = null,
)

/**
 * Payload returned by `POST /v2/startArchive` and `POST /v2/stopArchive`.
 */
@Serializable
data class ArchiveOperationResponse(
    @SerialName("id")
    val id: String,
    @SerialName("status")
    val status: String,
)

/**
 * Payload returned by `POST /v2/searchArchives`.
 */
@Serializable
data class SearchArchivesResponse(
    @SerialName("items")
    val items: List<ServerArchive>,
    @SerialName("count")
    val count: Int = 0,
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
