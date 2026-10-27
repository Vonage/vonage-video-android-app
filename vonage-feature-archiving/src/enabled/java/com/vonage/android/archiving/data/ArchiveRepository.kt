package com.vonage.android.archiving.data

import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveStatus
import com.vonage.android.archiving.ArchiveType
import com.vonage.android.shared.network.SessionKeyRequest
import com.vonage.logger.vonageLogger

private const val TAG = "ArchiveRepository"

/**
 * Talks to the v2 archiving endpoints. Every call is scoped by the session key JWT;
 * the key itself is never logged.
 */
class ArchiveRepository(
    private val archivingApi: ArchivingApi,
) {

    suspend fun getRecordings(sessionKey: String): Result<List<Archive>> =
        runCatching {
            val response = archivingApi.searchArchives(SessionKeyRequest(sessionKey))
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.result.data.items.map { a -> a.toModel() })
                } ?: run {
                    vonageLogger.e(TAG, "getRecordings: empty response body")
                    Result.failure(Exception("Empty response"))
                }
            } else {
                vonageLogger.e(
                    TAG,
                    "getRecordings: HTTP ${response.code()}, body=${response.errorBody()?.string()}",
                )
                Result.failure(Exception("Failed getting archives"))
            }
        }.onFailure {
            vonageLogger.e(TAG, "getRecordings: exception", it)
        }

    /**
     * Starts an archive. A transcription ([withTranscription]) always sends [withSummary]
     * explicitly, so the backend's own summary default never applies.
     */
    suspend fun startArchive(
        sessionKey: String,
        withTranscription: Boolean = false,
        withSummary: Boolean = false,
    ): Result<ArchiveId> =
        runCatching {
            val request = StartArchiveRequest(
                sessionKey = sessionKey,
                withTranscription = withTranscription,
                archiveOptions = if (withTranscription) {
                    StartArchiveOptions(TranscriptionProperties(hasSummary = withSummary))
                } else {
                    null
                },
            )
            val response = archivingApi.startArchiving(request)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(ArchiveId(it.result.data.id))
                } ?: run {
                    vonageLogger.e(TAG, "startArchive: empty response body")
                    Result.failure(Exception("Empty response"))
                }
            } else {
                vonageLogger.e(
                    TAG,
                    "startArchive: HTTP ${response.code()}, body=${response.errorBody()?.string()}",
                )
                Result.failure(Exception("Failed to start archiving"))
            }
        }.onFailure {
            vonageLogger.e(TAG, "startArchive: exception", it)
        }

    /**
     * Stops an archive. When [archiveId] is null the backend falls back to the archive it has
     * stored for the session (e.g. a recording started by another participant).
     */
    suspend fun stopArchive(sessionKey: String, archiveId: ArchiveId?): Result<Boolean> =
        runCatching {
            val response = archivingApi.stopArchiving(StopArchiveRequest(sessionKey, archiveId?.id))
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                vonageLogger.e(
                    TAG,
                    "stopArchive: HTTP ${response.code()}, archiveId=${archiveId?.id}, " +
                        "body=${response.errorBody()?.string()}",
                )
                Result.failure(Exception("Failed to stop archiving"))
            }
        }.onFailure {
            vonageLogger.e(TAG, "stopArchive: exception, archiveId=${archiveId?.id}", it)
        }.getOrElse { Result.failure(it) }
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
