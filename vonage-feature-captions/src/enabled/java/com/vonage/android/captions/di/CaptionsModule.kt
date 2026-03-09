package com.vonage.android.captions.di

import com.vonage.android.captions.EnabledVonageCaptions
import com.vonage.android.captions.VonageCaptions
import com.vonage.android.captions.data.CaptionsApi
import com.vonage.android.captions.data.CaptionsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CaptionsModule {

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): CaptionsApi = retrofit
        .create(CaptionsApi::class.java)

    @Provides
    @Singleton
    fun provideVonageCaptions(captionsRepository: CaptionsRepository): VonageCaptions =
        EnabledVonageCaptions(
            captionsRepository = captionsRepository,
        )
}
