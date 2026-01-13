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

enum class ArchiveStatus {
    AVAILABLE,
    PENDING,
    FAILED,
}
