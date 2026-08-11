package com.vonage.android.di

import android.content.Context
import com.vonage.android.fx.data.BackgroundEffectsRepository
import com.vonage.android.fx.data.DefaultBackgroundEffectsRepository
import com.vonage.android.fx.data.DefaultUserBackgroundRepository
import com.vonage.android.fx.data.GetBackgroundsUseCase
import com.vonage.android.fx.data.UserBackgroundRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VideoEffectsModule {

    @Provides
    @Singleton
    fun provideBackgroundEffectsRepository(
        @ApplicationContext context: Context,
    ): BackgroundEffectsRepository = DefaultBackgroundEffectsRepository(context)

    @Provides
    @Singleton
    fun provideUserBackgroundRepository(
        @ApplicationContext context: Context,
    ): UserBackgroundRepository = DefaultUserBackgroundRepository(context)

    @Provides
    @Singleton
    fun provideGetBackgroundsUseCase(
        backgroundEffectsRepository: BackgroundEffectsRepository,
        userBackgroundRepository: UserBackgroundRepository,
    ): GetBackgroundsUseCase = GetBackgroundsUseCase(backgroundEffectsRepository, userBackgroundRepository)
}
