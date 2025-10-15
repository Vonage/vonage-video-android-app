package com.vonage.android.chat

import com.vonage.android.kotlin.signal.ChatSignalPlugin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ChatModule {

    @Provides
    fun provideChatFeature(): ChatFeature =
        DisabledChatFeature()

    @Provides
    fun provideChatSignalPlugin(): ChatSignalPlugin =
        DisabledChatSignalPlugin()
}
