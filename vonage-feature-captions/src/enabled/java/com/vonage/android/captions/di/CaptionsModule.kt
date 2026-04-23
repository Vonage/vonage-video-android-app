package com.vonage.android.captions.di

import com.vonage.android.captions.EnabledVonageCaptions
import com.vonage.android.captions.VonageCaptions
import com.vonage.android.captions.data.CaptionsApi
import com.vonage.android.captions.data.CaptionsRepository
import retrofit2.Retrofit

object CaptionsModule {

    fun provideVonageCaptions(retrofit: Retrofit): VonageCaptions =
        EnabledVonageCaptions(
            captionsRepository = CaptionsRepository(provideApiService(retrofit)),
        )

    private fun provideApiService(retrofit: Retrofit): CaptionsApi = retrofit
        .create(CaptionsApi::class.java)
}
