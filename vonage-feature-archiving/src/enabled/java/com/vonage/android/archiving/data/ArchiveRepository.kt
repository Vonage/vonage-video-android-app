package com.vonage.android.archiving.data

import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveStatus
import com.vonage.android.archiving.ArchiveType
import com.vonage.android.vera.api.VeraApiException
import com.vonage.android.vera.api.archiving.ArchivingApi
import com.vonage.android.vera.api.archiving.ServerArchive
import com.vonage.logger.vonageLogger

private const val TAG = "ArchiveRepository"

/**
 * Maps the v2 archiving endpoints to the archiving domain model. Every call is scoped by the
 * session key JWT; the key itself is never logged.
 */
class ArchiveRepository(
    private val archivingApi: ArchivingApi,
) {

    suspend fun getRecordings(sessionKey: String): Result<List<Archive>> =
        archivingApi.searchArchives(sessionKey)
            .map { archives -> archives.map { it.toModel() } }
            .onFailure { logFailure("getRecordings", it) }

    /**
     * Starts an archive. A transcription ([withTranscription]) always sends [withSummary]
     * explicitly, so the backend's own summary default never applies.
     */
    suspend fun startArchive(
        sessionKey: String,
        withTranscription: Boolean = false,
        withSummary: Boolean = false,
    ): Result<ArchiveId> =
        archivingApi.startArchive(sessionKey, withTranscription, withSummary)
            .map { ArchiveId(it) }
            .onFailure { logFailure("startArchive", it) }

    /**
     * Stops an archive. When [archiveId] is null the backend falls back to the archive it has
     * stored for the session (e.g. a recording started by another participant).
     */
    suspend fun stopArchive(sessionKey: String, archiveId: ArchiveId?): Result<Boolean> =
        archivingApi.stopArchive(sessionKey, archiveId?.id)
            .map { true }
            .onFailure { logFailure("stopArchive", it, ", archiveId=${archiveId?.id}") }

    private fun logFailure(operation: String, error: Throwable, detail: String = "") {
        if (error is VeraApiException) {
            vonageLogger.e(
                TAG,
                "$operation: ${error.message}, HTTP ${error.httpCode}$detail, body=${error.errorBody}",
            )
        } else {
            vonageLogger.e(TAG, "$operation: exception$detail", error)
        }
    }
}

private fun ServerArchive.toModel() =
    if (hasTranscription) {
        toTranscriptionModel()
    } else {
        Archive(
            id = ArchiveId(id),
            name = name,
            url = url.orEmpty(),
            status = status.toArchiveStatus(),
            createdAt = createdAt,
            duration = duration,
            size = size,
        )
    }

/**
 * A transcription archive is downloaded from its transcript URL, which is only ready once
 * `transcription.status` is `available`: until then (including before the backend adds the
 * `transcription` object) it stays pending, so the goodbye screen keeps polling.
 */
private fun ServerArchive.toTranscriptionModel(): Archive {
    val archiveStatus = status.toArchiveStatus()
    // Local copy: ServerArchive lives in another module, so its property can't be smart cast.
    val transcription = transcription
    return Archive(
        id = ArchiveId(id),
        name = name,
        url = transcription?.url.orEmpty(),
        status = when {
            archiveStatus == ArchiveStatus.FAILED -> ArchiveStatus.FAILED
            transcription == null -> ArchiveStatus.PENDING
            else -> transcription.status.toArchiveStatus()
        },
        createdAt = createdAt,
        duration = duration,
        size = size,
        type = ArchiveType.TRANSCRIPTION,
    )
}

private fun String.toArchiveStatus(): ArchiveStatus =
    when (this) {
        "available" -> ArchiveStatus.AVAILABLE
        "requested", "started", "stopped", "uploaded", "paused" -> ArchiveStatus.PENDING
        else -> ArchiveStatus.FAILED
    }
