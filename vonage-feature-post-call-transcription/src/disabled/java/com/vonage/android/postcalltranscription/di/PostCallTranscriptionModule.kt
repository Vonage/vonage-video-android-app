package com.vonage.android.postcalltranscription.di

import com.vonage.android.postcalltranscription.DisabledVonagePostCallTranscription
import com.vonage.android.postcalltranscription.VonagePostCallTranscription

object PostCallTranscriptionModule {

    fun provideVonagePostCallTranscription(): VonagePostCallTranscription =
        DisabledVonagePostCallTranscription()
}
