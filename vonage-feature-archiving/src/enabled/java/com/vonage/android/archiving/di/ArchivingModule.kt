package com.vonage.android.archiving.di

import com.vonage.android.archiving.EnabledVonageArchiving
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.archiving.data.ArchiveRepository
import com.vonage.android.archiving.data.ArchivingApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ArchivingModule {

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ArchivingApi = retrofit
        .create(ArchivingApi::class.java)

    @Provides
    @Singleton
    fun provideVonageArchiving(archiveRepository: ArchiveRepository): VonageArchiving =
        EnabledVonageArchiving(
            archiveRepository = archiveRepository
        )
}
