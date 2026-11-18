package com.vonage.android.captions.di

import com.vonage.android.captions.DisabledVonageCaptions
import com.vonage.android.captions.VonageCaptions
import com.vonage.android.vera.api.VeraApiClient

object CaptionsModule {

    fun provideVonageCaptions(@Suppress("UNUSED_PARAMETER") apiClient: VeraApiClient): VonageCaptions =
        DisabledVonageCaptions()

}
