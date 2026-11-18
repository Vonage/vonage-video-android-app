package com.vonage.android.meetingroom.internal.factory

import com.vonage.android.captions.VonageCaptions
import com.vonage.android.captions.di.CaptionsModule
import com.vonage.android.vera.api.VeraApiClient

internal fun createVonageCaptions(apiClient: VeraApiClient): VonageCaptions =
    CaptionsModule.provideVonageCaptions(apiClient)
