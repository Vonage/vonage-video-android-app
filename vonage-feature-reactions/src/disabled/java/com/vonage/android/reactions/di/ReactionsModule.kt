package com.vonage.android.reactions.di

import com.vonage.android.reactions.DisabledReactionSignalPlugin
import com.vonage.android.reactions.ReactionSignalPlugin

object ReactionsModule {

    fun provideReactionSignalPlugin(): ReactionSignalPlugin =
        DisabledReactionSignalPlugin()

}
