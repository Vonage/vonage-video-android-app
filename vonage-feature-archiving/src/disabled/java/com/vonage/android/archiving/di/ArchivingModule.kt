package com.vonage.android.archiving.di

import com.vonage.android.archiving.DisabledVonageArchiving
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.vera.api.VeraApiClient

object ArchivingModule {

    fun provideVonageArchiving(@Suppress("UNUSED_PARAMETER") apiClient: VeraApiClient): VonageArchiving =
        DisabledVonageArchiving()
}
