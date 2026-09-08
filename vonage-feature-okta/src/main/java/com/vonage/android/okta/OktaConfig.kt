package com.vonage.android.okta

/**
 * OIDC client configuration for the Okta identity provider.
 *
 * Values come from `local.properties` / environment variables at build time
 * (see docs/AUTHENTICATION.md) and must never be committed to the repository.
 *
 * @property issuerUrl Okta authorization server URL, e.g. `https://your-org.okta.com`.
 * @property clientId OIDC client ID registered in the Okta application.
 * @property signInRedirectUri Redirect URI Okta calls back after authentication,
 *   e.g. `com.vonage.android:/callback`. Its scheme must match the
 *   `webAuthenticationRedirectScheme` manifest placeholder.
 * @property scope Space-separated OIDC scopes.
 */
data class OktaConfig(
    val issuerUrl: String = "",
    val clientId: String = "",
    val signInRedirectUri: String = "",
    val scope: String = DEFAULT_SCOPE,
) {
    val isValid: Boolean
        get() = issuerUrl.isNotBlank() && clientId.isNotBlank() && signInRedirectUri.isNotBlank()

    companion object {
        const val DEFAULT_SCOPE = "openid profile offline_access"
    }
}
