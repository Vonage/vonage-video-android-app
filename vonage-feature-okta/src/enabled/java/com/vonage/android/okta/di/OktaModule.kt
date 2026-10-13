package com.vonage.android.okta.di

import android.content.Context
import com.vonage.android.okta.EnabledVonageOktaAuth
import com.vonage.android.okta.OktaConfig
import com.vonage.android.okta.VonageOktaAuth
import com.vonage.android.okta.data.FakeBrowserSignInProvider
import com.vonage.android.okta.data.OktaBrowserSignInProvider
import com.vonage.android.okta.data.SwitchableBrowserSignInProvider

object OktaModule {

    /**
     * @param isFakeSignInEnabled E2E-only: while it returns true, sign-in succeeds instantly
     *   with a fake user instead of opening the Okta browser flow.
     */
    fun provideVonageOktaAuth(
        context: Context,
        config: OktaConfig,
        isFakeSignInEnabled: () -> Boolean = { false },
    ): VonageOktaAuth =
        EnabledVonageOktaAuth(
            browserSignIn = SwitchableBrowserSignInProvider(
                real = OktaBrowserSignInProvider(
                    applicationContext = context.applicationContext,
                    config = config,
                ),
                fake = FakeBrowserSignInProvider(),
                useFake = isFakeSignInEnabled,
            ),
        )
}
