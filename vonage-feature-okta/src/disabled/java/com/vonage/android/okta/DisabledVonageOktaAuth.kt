package com.vonage.android.okta

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Inert [VonageOktaAuth] used when the Okta feature is compiled out.
 *
 * Always reports [AuthState.NotAuthenticated] and provides no token, so backend
 * requests are sent without an `Authorization` header.
 */
class DisabledVonageOktaAuth : VonageOktaAuth {

    override val isCapable: Boolean = false

    private val _authState = MutableStateFlow<AuthState>(AuthState.NotAuthenticated)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun restoreSession() {
        // No-op: nothing to restore when the feature is disabled.
    }

    override suspend fun signIn(context: Context): Result<Unit> =
        Result.failure(IllegalStateException("Okta authentication feature is disabled"))

    override suspend fun signOut(): Result<Unit> = Result.success(Unit)

    override suspend fun currentToken(): String? = null
}
