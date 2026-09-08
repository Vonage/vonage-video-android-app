package com.vonage.android.okta.ui

/**
 * Test tags for the authentication UI.
 *
 * Kebab-case values are deliberately aligned with the iOS accessibility identifiers
 * so Maestro E2E flows can be shared between platforms.
 */
object AuthTestTags {
    const val AUTH_BUTTON_TAG = "auth-button"
    const val SIGN_IN_SHEET_TAG = "auth-sign-in-screen"
    const val SIGN_IN_TITLE_TAG = "auth-sign-in-title"
    const val SIGN_IN_SUBTITLE_TAG = "auth-sign-in-subtitle"
    const val SIGN_IN_ERROR_TAG = "auth-sign-in-error"
    const val SIGN_IN_PROVIDER_TAG_PREFIX = "auth-sign-in-provider-"
    const val ACCOUNT_MENU_TAG = "auth-account-menu"
    const val ACCOUNT_NAME_TAG = "auth-account-name"
    const val SIGN_OUT_BUTTON_TAG = "auth-sign-out-button"
}
