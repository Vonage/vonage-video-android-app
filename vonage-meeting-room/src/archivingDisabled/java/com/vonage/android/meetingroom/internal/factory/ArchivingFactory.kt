package com.vonage.android.meetingroom.internal.factory

import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.archiving.di.ArchivingModule
import com.vonage.android.vera.api.VeraApiClient

@Suppress("UNUSED_PARAMETER")
internal fun createVonageArchiving(apiClient: VeraApiClient): VonageArchiving =
    ArchivingModule.provideVonageArchiving(apiClient)
