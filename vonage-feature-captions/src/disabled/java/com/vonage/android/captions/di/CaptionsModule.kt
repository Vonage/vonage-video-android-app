package com.vonage.android.captions.di

import com.vonage.android.captions.DisabledVonageCaptions
import com.vonage.android.captions.VonageCaptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CaptionsModule {

    @Provides
    @Singleton
    fun provideVonageCaptions(): VonageCaptions = DisabledVonageCaptions()

}
