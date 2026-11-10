package com.vonage.android.archiving.di

import com.vonage.android.archiving.EnabledVonageArchiving
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.archiving.data.ArchiveRepository
import com.vonage.android.vera.api.VeraApiClient

object ArchivingModule {

    fun provideVonageArchiving(apiClient: VeraApiClient): VonageArchiving =
        EnabledVonageArchiving(
            archiveRepository = ArchiveRepository(apiClient.archiving)
        )
}
