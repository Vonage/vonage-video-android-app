package com.vonage.android.screensharing.di

import android.content.Context
import com.vonage.android.screensharing.EnabledScreenSharing
import com.vonage.android.screensharing.VonageScreenSharing
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScreensharingModule {

    @Provides
    @Singleton
    fun provideVonageScreenSharing(
        @ApplicationContext context: Context
    ): VonageScreenSharing =
        EnabledScreenSharing(
            context = context,
        )

}
