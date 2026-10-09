package com.vonage.android.captions.di

import com.vonage.android.captions.DisabledVonageCaptions
import com.vonage.android.captions.VonageCaptions
import retrofit2.Retrofit

object CaptionsModule {

    fun provideVonageCaptions(@Suppress("UNUSED_PARAMETER") retrofit: Retrofit): VonageCaptions =
        DisabledVonageCaptions()

}
