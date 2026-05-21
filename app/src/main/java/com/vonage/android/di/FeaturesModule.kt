package com.vonage.android.di

import android.content.Context
import com.vonage.android.archiving.VonageArchiving
import com.vonage.android.archiving.di.ArchivingModule
import com.vonage.android.captions.VonageCaptions
import com.vonage.android.captions.di.CaptionsModule
import com.vonage.android.chat.ChatFeature
import com.vonage.android.chat.ChatModule
import com.vonage.android.fx.data.UserBackgroundRepository
import com.vonage.android.kotlin.signal.ChatSignalPlugin
import com.vonage.android.reactions.ReactionSignalPlugin
import com.vonage.android.reactions.di.ReactionsModule
import com.vonage.android.screensharing.VonageScreenSharing
import com.vonage.android.screensharing.di.ScreenSharingModule
import com.vonage.android.settings.CallSettingsHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeaturesModule {

    @Singleton
    @Provides
    fun provideVonageArchiving(retrofit: Retrofit): VonageArchiving =
        ArchivingModule.provideVonageArchiving(retrofit)

    @Singleton
    @Provides
    fun provideVonageCaptions(retrofit: Retrofit): VonageCaptions =
        CaptionsModule.provideVonageCaptions(retrofit)

    @Provides
    fun provideChatFeature(): ChatFeature =
        ChatModule.provideChatFeature()

    @Provides
    fun provideChatSignalPlugin(
        @ApplicationContext context: Context,
    ): ChatSignalPlugin =
        ChatModule.provideChatSignalPlugin(context)

    @Provides
    fun provideReactionSignalPlugin(): ReactionSignalPlugin =
        ReactionsModule.provideReactionSignalPlugin()

    @Singleton
    @Provides
    fun provideVonageScreenSharing(
        @ApplicationContext context: Context,
    ): VonageScreenSharing =
        ScreenSharingModule.provideVonageScreenSharing(context)

    @Singleton
    @Provides
    fun provideCallSettingsHolder(): CallSettingsHolder = CallSettingsHolder()

    @Singleton
    @Provides
    fun provideUserBackgroundRepository(
        @ApplicationContext context: Context,
    ): UserBackgroundRepository = UserBackgroundRepository(context)
}
