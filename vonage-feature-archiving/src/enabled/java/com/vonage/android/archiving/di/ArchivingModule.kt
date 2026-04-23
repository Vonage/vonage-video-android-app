package com.vonage.android.archiving.di

import com.vonage.android.archiving.EnabledVonageArchiving
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.archiving.data.ArchiveRepository
import com.vonage.android.archiving.data.ArchivingApi
import retrofit2.Retrofit

object ArchivingModule {

    fun provideApiService(retrofit: Retrofit): ArchivingApi = retrofit
        .create(ArchivingApi::class.java)

    fun provideVonageArchiving(retrofit: Retrofit): VonageArchiving =
        EnabledVonageArchiving(
            archiveRepository = ArchiveRepository(provideApiService(retrofit))
        )
}
