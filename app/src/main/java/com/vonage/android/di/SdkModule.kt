package com.vonage.android.di

import com.vonage.android.kotlin.PublisherFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SdkModule {

    @Provides
    fun providePublisherFactory(): PublisherFactory = PublisherFactory()

}
