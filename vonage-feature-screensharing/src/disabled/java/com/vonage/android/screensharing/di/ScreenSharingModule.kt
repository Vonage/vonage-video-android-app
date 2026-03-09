package com.vonage.android.screensharing.di

import com.vonage.android.screensharing.DisabledScreenSharing
import com.vonage.android.screensharing.VonageScreenSharing
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScreenSharingModule {

    @Provides
    @Singleton
    fun provideVonageScreenSharing(): VonageScreenSharing =
        DisabledScreenSharing()

}
