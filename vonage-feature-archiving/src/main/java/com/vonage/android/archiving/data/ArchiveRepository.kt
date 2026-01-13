package com.vonage.android.archiving.data

import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveStatus
import javax.inject.Inject

class ArchiveRepository @Inject constructor(
    private val apiService: ArchivingApi,
) {

    @Suppress("TooGenericExceptionCaught")
    suspend fun getRecordings(roomName: String): Result<List<Archive>> =
        runCatching {
            val response = apiService.getArchives(roomName)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.archives.map { a -> a.toModel() })
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed getting archives"))
            }
        }

    suspend fun startArchive(roomName: String): Result<ArchiveId> =
        runCatching {
            val response = apiService.startArchiving(roomName)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(ArchiveId(it.archiveId))
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to start archiving"))
            }
        }

    suspend fun stopArchive(roomName: String, archiveId: ArchiveId): Result<Boolean> =
        runCatching {
            val response = apiService.stopArchiving(roomName, archiveId.id)
            return if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to stop archiving"))
            }
        }
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
