package com.vonage.android.archiving

import androidx.compose.runtime.Immutable

@Immutable
data class Archive(
    val id: ArchiveId,
    val duration: Int,
    val size: Int,
    val name: String,
    /** Download URL. For a transcription it is the transcript, not the archived audio. */
    val url: String,
    /** For a transcription, the status of the transcript, which becomes available after the audio. */
    val status: ArchiveStatus,
    val createdAt: Long,
    val type: ArchiveType = ArchiveType.RECORDING,
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
