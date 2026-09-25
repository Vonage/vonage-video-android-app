package com.vonage.android.archiving.data

import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveStatus
import com.vonage.logger.vonageLogger

private const val TAG = "ArchiveRepository"

class ArchiveRepository(
    private val archivingApi: ArchivingApi,
) {

    suspend fun getRecordings(roomName: String): Result<List<Archive>> =
        runCatching {
            val response = archivingApi.getArchives(roomName)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.archives.map { a -> a.toModel() })
                } ?: run {
                    vonageLogger.e(TAG, "getRecordings: empty response body for room=$roomName")
                    Result.failure(Exception("Empty response"))
                }
            } else {
                vonageLogger.e(
                    TAG,
                    "getRecordings: HTTP ${response.code()} for room=$roomName, " +
                        "body=${response.errorBody()?.string()}",
                )
                Result.failure(Exception("Failed getting archives"))
            }
        }.onFailure {
            vonageLogger.e(TAG, "getRecordings: exception for room=$roomName", it)
        }

    suspend fun startArchive(roomName: String): Result<ArchiveId> =
        runCatching {
            val response = archivingApi.startArchiving(roomName)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(ArchiveId(it.archiveId))
                } ?: run {
                    vonageLogger.e(TAG, "startArchive: empty response body for room=$roomName")
                    Result.failure(Exception("Empty response"))
                }
            } else {
                vonageLogger.e(
                    TAG,
                    "startArchive: HTTP ${response.code()} for room=$roomName, " +
                        "body=${response.errorBody()?.string()}",
                )
                Result.failure(Exception("Failed to start archiving"))
            }
        }.onFailure {
            vonageLogger.e(TAG, "startArchive: exception for room=$roomName", it)
        }

    suspend fun stopArchive(roomName: String, archiveId: ArchiveId): Result<Boolean> =
        runCatching {
            val response = archivingApi.stopArchiving(roomName, archiveId.id)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                vonageLogger.e(
                    TAG,
                    "stopArchive: HTTP ${response.code()} for room=$roomName, " +
                        "archiveId=${archiveId.id}, body=${response.errorBody()?.string()}",
                )
                Result.failure(Exception("Failed to stop archiving"))
            }
        }.onFailure {
            vonageLogger.e(TAG, "stopArchive: exception for room=$roomName, archiveId=${archiveId.id}", it)
        }.getOrElse { Result.failure(it) }
}

private fun ServerArchive.toModel() =
    Archive(
        id = ArchiveId(id),
        name = name,
        url = url.orEmpty(),
        status = status.toArchiveStatus(),
        createdAt = createdAt,
        duration = duration,
        size = size,
    )

private fun String.toArchiveStatus(): ArchiveStatus =
    when (this) {
        "available" -> ArchiveStatus.AVAILABLE
        "started", "stopped", "uploaded", "paused" -> ArchiveStatus.PENDING
        else -> ArchiveStatus.FAILED
    }
