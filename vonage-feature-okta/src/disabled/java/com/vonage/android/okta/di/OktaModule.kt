package com.vonage.android.okta.di

import android.content.Context
import com.vonage.android.okta.DisabledVonageOktaAuth
import com.vonage.android.okta.OktaConfig
import com.vonage.android.okta.VonageOktaAuth

object OktaModule {

    fun provideVonageOktaAuth(
        @Suppress("UNUSED_PARAMETER") context: Context,
        @Suppress("UNUSED_PARAMETER") config: OktaConfig,
    ): VonageOktaAuth = DisabledVonageOktaAuth()
}
