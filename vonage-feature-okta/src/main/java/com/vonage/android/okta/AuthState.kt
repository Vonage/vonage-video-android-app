package com.vonage.android.okta

/**
 * The user identity resolved from the identity provider after sign-in.
 *
 * @property name Display name from the ID token, or null when the provider did not supply one.
 */
data class AuthenticatedUser(
    val name: String? = null,
)

/**
 * Authentication state exposed by [VonageOktaAuth].
 */
sealed interface AuthState {

    data object NotAuthenticated : AuthState

    data class Authenticated(override val user: AuthenticatedUser) : AuthState

    val isAuthenticated: Boolean
        get() = this is Authenticated

    /** The authenticated user, or null when not authenticated. */
    val user: AuthenticatedUser?
        get() = null
}
