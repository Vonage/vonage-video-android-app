package com.vonage.android.archiving.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Body for `POST /v2/startArchive`.
 *
 * [withTranscription] asks the backend for a transcription archive instead of a recording; it
 * applies the matching archive options (name, output mode, `multiArchiveTag`, summary). No
 * `archiveOptions` are sent: the backend replaces its merged defaults with the client's
 * `archiveOptions`, which would start an unnamed recording instead of a transcription. Defaults
 * are not encoded, so a recording request only carries the session key.
 *
 * [withSummary] is only sent with a transcription: `false` asks the backend to drop the AI summary
 * (`transcriptionProperties.hasSummary`), which it otherwise includes. A backend without
 * `withSummary` support ignores the key and keeps the summary.
 */
@Serializable
data class StartArchiveRequest(
    @SerialName("sessionKey")
    val sessionKey: String,
    @SerialName("withTranscription")
    val withTranscription: Boolean = false,
    @SerialName("withSummary")
    val withSummary: Boolean? = null,
)

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
    /** `true` for a post-call transcription archive; its transcript is described by [transcription]. */
    @SerialName("hasTranscription")
    val hasTranscription: Boolean = false,
    /** Absent until the backend starts generating the transcript. */
    @SerialName("transcription")
    val transcription: ServerTranscription? = null,
)

/** Transcript of a transcription archive; [url] is the transcript download (`transcription.zip`). */
@Serializable
data class ServerTranscription(
    @SerialName("status")
    val status: String,
    @SerialName("url")
    val url: String? = null,
)
