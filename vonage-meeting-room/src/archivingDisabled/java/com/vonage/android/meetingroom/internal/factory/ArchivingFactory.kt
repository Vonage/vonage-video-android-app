package com.vonage.android.meetingroom.internal.factory

import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.archiving.di.ArchivingModule
import retrofit2.Retrofit

@Suppress("UNUSED_PARAMETER")
internal fun createVonageArchiving(retrofit: Retrofit): VonageArchiving =
    ArchivingModule.provideVonageArchiving(retrofit)
