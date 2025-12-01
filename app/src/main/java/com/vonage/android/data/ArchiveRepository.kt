package com.vonage.android.data

import com.vonage.android.data.network.APIService
import com.vonage.android.data.network.ServerArchive
import javax.inject.Inject

class ArchiveRepository @Inject constructor(
    private val apiService: APIService,
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

    suspend fun startArchive(roomName: String): Result<String> =
        runCatching {
            val response = apiService.startArchiving(roomName)
            return if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.archiveId)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to start archiving"))
            }
        }

    suspend fun stopArchive(roomName: String, archiveId: String): Result<Boolean> =
        runCatching {
            val response = apiService.stopArchiving(roomName, archiveId)
            return if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to stop archiving"))
            }
        }
}

fun ServerArchive.toModel() =
    Archive(
        id = id,
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

data class Archive(
    val id: String,
    val duration: Int,
    val size: Int,
    val name: String,
    val url: String,
    val status: ArchiveStatus,
    val createdAt: Long,
)

enum class ArchiveStatus {
    AVAILABLE,
    PENDING,
    FAILED
}
