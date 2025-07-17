package com.vonage.android.di

import android.content.Context
import com.vonage.android.kotlin.PublisherFactory
import com.vonage.android.kotlin.VonageVideoClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SdkModule {

    @Provides
    fun providePublisherFactory(): PublisherFactory = PublisherFactory()

    @Singleton
    @Provides
    fun provideVonageVideoClient(
        @ApplicationContext context: Context,
    ): VonageVideoClient =
        VonageVideoClient(context)

}
