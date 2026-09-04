package com.vonage.android.di

import android.content.Context
import com.vonage.android.BuildConfig
import com.vonage.android.okta.OktaConfig
import com.vonage.android.okta.VonageOktaAuth
import com.vonage.android.okta.di.OktaModule
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@EntryPoint
@InstallIn(SingletonComponent::class)
interface VonageOktaAuthEntryPoint {
    fun vonageOktaAuth(): VonageOktaAuth
}

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideVonageOktaAuth(
        @ApplicationContext context: Context,
    ): VonageOktaAuth = OktaModule.provideVonageOktaAuth(
        context = context,
        config = OktaConfig(
            issuerUrl = BuildConfig.OKTA_ISSUER_URL,
            clientId = BuildConfig.OKTA_CLIENT_ID,
            signInRedirectUri = BuildConfig.OKTA_SIGN_IN_REDIRECT_URI,
            scope = BuildConfig.OKTA_SCOPE.ifBlank { OktaConfig.DEFAULT_SCOPE },
        ),
    )
}
