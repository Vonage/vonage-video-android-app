package com.vonage.android.chat

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ChatModule {

    @Provides
    fun provideChatFeature(): ChatFeature =
        EnabledChatFeature()

}
