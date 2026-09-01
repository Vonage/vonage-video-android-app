package com.vonage.android.okta

import android.content.Context
import com.vonage.android.okta.data.BrowserSignInProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Okta-backed implementation of [VonageOktaAuth].
 *
 * All Okta SDK interaction is delegated to a [BrowserSignInProvider] so this class
 * stays a thin, unit-testable state machine over sign-in/sign-out results.
 */
class EnabledVonageOktaAuth(
    private val browserSignIn: BrowserSignInProvider,
) : VonageOktaAuth {

    override val isCapable: Boolean = true

    private val _authState = MutableStateFlow<AuthState>(AuthState.NotAuthenticated)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun restoreSession() {
        val session = runCatching { browserSignIn.restoreSession() }.getOrNull()
        _authState.value = session
            ?.let { AuthState.Authenticated(AuthenticatedUser(name = it.userName)) }
            ?: AuthState.NotAuthenticated
    }

    override suspend fun signIn(context: Context): Result<Unit> =
        browserSignIn.signIn(context).map { result ->
            _authState.value = AuthState.Authenticated(AuthenticatedUser(name = result.userName))
        }

    override suspend fun signOut(): Result<Unit> =
        browserSignIn.removeCredential().onSuccess {
            _authState.value = AuthState.NotAuthenticated
        }

    override suspend fun currentToken(): String? = browserSignIn.currentToken()
}
