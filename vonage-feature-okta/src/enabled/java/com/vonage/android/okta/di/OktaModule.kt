package com.vonage.android.okta.di

import android.content.Context
import com.vonage.android.okta.EnabledVonageOktaAuth
import com.vonage.android.okta.OktaConfig
import com.vonage.android.okta.VonageOktaAuth
import com.vonage.android.okta.data.OktaBrowserSignInProvider

object OktaModule {

    fun provideVonageOktaAuth(context: Context, config: OktaConfig): VonageOktaAuth =
        EnabledVonageOktaAuth(
            browserSignIn = OktaBrowserSignInProvider(
                applicationContext = context.applicationContext,
                config = config,
            ),
        )
}
