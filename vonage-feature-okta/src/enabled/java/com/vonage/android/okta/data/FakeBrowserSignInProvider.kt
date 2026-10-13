package com.vonage.android.okta.data

import android.content.Context

/**
 * E2E-only [BrowserSignInProvider] that signs in instantly without opening the browser,
 * so Maestro flows can exercise the sign-in / sign-out UI without a real Okta tenant.
 *
 * The credential lives in memory only, so every app launch starts signed out.
 */
internal class FakeBrowserSignInProvider : BrowserSignInProvider {

    @Volatile
    private var session: SignInResult? = null

    override suspend fun signIn(context: Context): Result<SignInResult> =
        Result.success(SignInResult(accessToken = FAKE_ACCESS_TOKEN, userName = FAKE_USER_NAME))
            .onSuccess { session = it }

    override suspend fun currentToken(): String? = session?.accessToken

    override suspend fun removeCredential(): Result<Unit> {
        session = null
        return Result.success(Unit)
    }

    override suspend fun restoreSession(): SignInResult? = session

    companion object {
        const val FAKE_USER_NAME = "E2E Test User"
        const val FAKE_ACCESS_TOKEN = "e2e-fake-access-token"
    }
}

/**
 * Routes every call to [fake] while [useFake] returns true, otherwise to [real].
 * [useFake] is evaluated per call because the E2E switch is read from the launch
 * intent, which may happen after this provider is created.
 */
internal class SwitchableBrowserSignInProvider(
    private val real: BrowserSignInProvider,
    private val fake: BrowserSignInProvider,
    private val useFake: () -> Boolean,
) : BrowserSignInProvider {

    private val delegate: BrowserSignInProvider
        get() = if (useFake()) fake else real

    override suspend fun signIn(context: Context): Result<SignInResult> = delegate.signIn(context)

    override suspend fun currentToken(): String? = delegate.currentToken()

    override suspend fun removeCredential(): Result<Unit> = delegate.removeCredential()

    override suspend fun restoreSession(): SignInResult? = delegate.restoreSession()
}
