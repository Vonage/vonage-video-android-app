package com.vonage.android.postcalltranscription.di

import com.vonage.android.postcalltranscription.EnabledVonagePostCallTranscription
import com.vonage.android.postcalltranscription.VonagePostCallTranscription

object PostCallTranscriptionModule {

    fun provideVonagePostCallTranscription(): VonagePostCallTranscription =
        EnabledVonagePostCallTranscription()
}
