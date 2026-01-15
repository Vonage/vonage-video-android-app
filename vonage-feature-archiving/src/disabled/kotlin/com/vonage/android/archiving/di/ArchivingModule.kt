package com.vonage.android.archiving.di

import com.vonage.android.archiving.DisabledVonageArchiving
import com.vonage.android.archiving.VonageArchiving
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ArchivingModule {

    @Provides
    @Singleton
    fun provideVonageArchiving(): VonageArchiving =
        DisabledVonageArchiving()
}
