package com.vonage.android.reactions.di

import com.vonage.android.reactions.DisabledReactionSignalPlugin
import com.vonage.android.reactions.ReactionSignalPlugin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ReactionsModule {

    @Provides
    fun provideReactionSignalPlugin(): ReactionSignalPlugin =
        DisabledReactionSignalPlugin()

}
