package com.vonage.android.archiving

import androidx.compose.runtime.Immutable

@Immutable
data class Archive(
    val id: ArchiveId,
    val duration: Int,
    val size: Int,
    val name: String,
    val url: String,
    val status: ArchiveStatus,
    val createdAt: Long,
)

@JvmInline
value class ArchiveId(val id: String)

/**
 * Kind of archive. A recording and a transcription can run at the same time; the backend tells
 * them apart by the archive [archiveName] (`multiArchiveTag`), reported in the session archive events.
 */
enum class ArchiveType(val archiveName: String) {
    RECORDING("recording"),
    TRANSCRIPTION("transcription"),
    ;

    companion object {
        /** Archives without the transcription name (including unnamed ones) are recordings. */
        fun fromArchiveName(name: String?): ArchiveType =
            if (name == TRANSCRIPTION.archiveName) TRANSCRIPTION else RECORDING
    }
}

enum class ArchiveStatus {
    AVAILABLE,
    PENDING,
    FAILED,
}
