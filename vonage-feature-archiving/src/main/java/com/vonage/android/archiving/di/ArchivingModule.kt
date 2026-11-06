package com.vonage.android.archiving.di

import com.vonage.android.archiving.ArchiveType
import com.vonage.android.archiving.DefaultVonageArchiving
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.archiving.data.ArchiveRepository
import com.vonage.android.archiving.data.ArchivingApi
import retrofit2.Retrofit

object ArchivingModule {

    /** Recording and transcription are enabled independently; with both off every call fails locally. */
    fun provideVonageArchiving(
        retrofit: Retrofit,
        recordingEnabled: Boolean,
        transcriptionEnabled: Boolean,
    ): VonageArchiving =
        DefaultVonageArchiving(
            archiveRepository = ArchiveRepository(provideApiService(retrofit)),
            enabledTypes = buildSet {
                if (recordingEnabled) add(ArchiveType.RECORDING)
                if (transcriptionEnabled) add(ArchiveType.TRANSCRIPTION)
            },
        )

    private fun provideApiService(retrofit: Retrofit): ArchivingApi = retrofit
        .create(ArchivingApi::class.java)
}
