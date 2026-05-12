package com.vonage.android.meetingroom.internal.factory

import com.vonage.android.captions.VonageCaptions
import com.vonage.android.captions.di.CaptionsModule
import retrofit2.Retrofit

@Suppress("UNUSED_PARAMETER")
internal fun createVonageCaptions(retrofit: Retrofit): VonageCaptions =
    CaptionsModule.provideVonageCaptions()
