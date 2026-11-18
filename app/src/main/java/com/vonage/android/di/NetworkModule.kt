package com.vonage.android.di

import com.vonage.android.BuildConfig
import com.vonage.android.data.network.OktaAuthTokenProvider
import com.vonage.android.data.network.interceptor.E2eForceUnauthorizedInterceptor
import com.vonage.android.data.network.veraUserAgent
import com.vonage.android.vera.api.VeraApiClient
import com.vonage.android.vera.api.feedback.FeedbackApi
import com.vonage.android.vera.api.session.SessionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideVeraApiClient(
        authTokenProvider: OktaAuthTokenProvider,
        e2eForceUnauthorizedInterceptor: E2eForceUnauthorizedInterceptor,
    ): VeraApiClient = VeraApiClient.Builder(BuildConfig.BASE_API_URL)
        .logBodies(BuildConfig.DEBUG)
        .userAgent(veraUserAgent())
        .authTokenProvider(authTokenProvider)
        // Runs after the Authorization header is attached; no-op unless an E2E flow enables it.
        .addInterceptor(e2eForceUnauthorizedInterceptor)
        .build()

    @Provides
    fun provideFeedbackApi(apiClient: VeraApiClient): FeedbackApi = apiClient.feedback

    /**
     * Session bootstrap (the meeting room builds its own client with the same endpoints).
     * On the app client, so the Authorization and E2E interceptors apply.
     */
    @Provides
    fun provideSessionApi(apiClient: VeraApiClient): SessionApi = apiClient.session

}
