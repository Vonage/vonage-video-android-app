package com.vonage.android.archiving.di

import com.vonage.android.archiving.DisabledVonageArchiving
import com.vonage.android.archiving.VonageArchiving
import retrofit2.Retrofit

object ArchivingModule {

    fun provideVonageArchiving(@Suppress("UNUSED_PARAMETER") retrofit: Retrofit): VonageArchiving =
        DisabledVonageArchiving()
}
