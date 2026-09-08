package com.vonage.android.okta

/**
 * An identity provider available for sign-in.
 *
 * Mirrors the iOS reference app's `IDProvider` so the sign-in UI can list
 * multiple providers in the future without changing its layout.
 */
data class IdProvider(
    val id: String,
    val displayName: String,
) {
    companion object {
        const val OKTA_ID = "okta"

        val okta = IdProvider(id = OKTA_ID, displayName = "Okta")
    }
}
