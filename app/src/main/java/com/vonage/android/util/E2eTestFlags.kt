package com.vonage.android.util

import android.content.Intent
import com.vonage.android.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Switches used only by Maestro E2E flows, read from the launch intent extras
 * (`launchApp: arguments:`). Everything is off unless a flow explicitly passes it,
 * so regular launches are unaffected.
 *
 * The extras are only honoured when [BuildConfig.E2E_HOOKS_ENABLED] is set: always in debug
 * builds, and in release only for the Maestro CI build (`-Pvonage.e2eHooks=true`).
 */
@Singleton
class E2eTestFlags internal constructor(
    private val hooksEnabled: Boolean,
) {

    @Inject
    constructor() : this(hooksEnabled = BuildConfig.E2E_HOOKS_ENABLED)

    /**
     * When true, the backend is simulated as enforcing authentication: signed-out
     * `session/{room}` requests are answered with `401` without reaching the network.
     */
    @Volatile
    var forceAuthRequired: Boolean = false
        private set

    /**
     * When true, "Sign in with Okta" succeeds instantly with a fake user instead of opening
     * the Okta browser flow, so sign-in / sign-out can be tested without a real Okta tenant.
     */
    @Volatile
    var fakeSignIn: Boolean = false
        private set

    fun updateFrom(intent: Intent?) {
        forceAuthRequired = hooksEnabled && intent.readFlag(FORCE_AUTH_REQUIRED_EXTRA)
        fakeSignIn = hooksEnabled && intent.readFlag(FAKE_SIGN_IN_EXTRA)
    }

    private fun Intent?.readFlag(key: String): Boolean {
        val extras = this?.extras ?: return false
        // Maestro passes YAML booleans as Boolean extras and quoted values as String extras.
        return extras.getBoolean(key, false) || extras.getString(key) == "true"
    }

    companion object {
        const val FORCE_AUTH_REQUIRED_EXTRA = "e2eForceAuthRequired"
        const val FAKE_SIGN_IN_EXTRA = "e2eFakeSignIn"
    }
}
