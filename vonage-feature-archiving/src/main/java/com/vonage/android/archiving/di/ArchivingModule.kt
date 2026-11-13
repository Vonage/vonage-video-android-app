package com.vonage.android.archiving.di

import com.vonage.android.archiving.ArchiveType
import com.vonage.android.archiving.DefaultVonageArchiving
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.archiving.data.ArchiveRepository
import com.vonage.android.vera.api.VeraApiClient

object ArchivingModule {

    /** Recording and transcription are enabled independently; with both off every call fails locally. */
    fun provideVonageArchiving(
        apiClient: VeraApiClient,
        recordingEnabled: Boolean,
        transcriptionEnabled: Boolean,
    ): VonageArchiving =
        DefaultVonageArchiving(
            archiveRepository = ArchiveRepository(apiClient.archiving),
            enabledTypes = buildSet {
                if (recordingEnabled) add(ArchiveType.RECORDING)
                if (transcriptionEnabled) add(ArchiveType.TRANSCRIPTION)
            },
        )
}
