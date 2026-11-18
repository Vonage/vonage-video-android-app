package com.vonage.android.captions.di

import com.vonage.android.captions.EnabledVonageCaptions
import com.vonage.android.captions.VonageCaptions
import com.vonage.android.captions.data.CaptionsRepository
import com.vonage.android.vera.api.VeraApiClient

object CaptionsModule {

    fun provideVonageCaptions(apiClient: VeraApiClient): VonageCaptions =
        EnabledVonageCaptions(
            captionsRepository = CaptionsRepository(apiClient.captions),
        )
}
