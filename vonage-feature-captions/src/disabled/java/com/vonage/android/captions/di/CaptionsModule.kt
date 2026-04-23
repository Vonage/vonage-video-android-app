package com.vonage.android.captions.di

import com.vonage.android.captions.DisabledVonageCaptions
import com.vonage.android.captions.VonageCaptions

object CaptionsModule {

    fun provideVonageCaptions(): VonageCaptions = DisabledVonageCaptions()

}
