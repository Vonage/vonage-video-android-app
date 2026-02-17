package com.vonage.android.archiving

import androidx.compose.runtime.Immutable
import java.text.SimpleDateFormat

@Immutable
data class ArchiveListStyle(
    val dateFormat: SimpleDateFormat,
    val containerTitle: String,
    val emptyLabel: String,
)

enum class ArchivingUiState {
    IDLE,
    STARTING,
    RECORDING,
    STOPPING,
}