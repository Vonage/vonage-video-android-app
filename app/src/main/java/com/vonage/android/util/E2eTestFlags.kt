package com.vonage.android.util

import android.content.Intent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Switches used only by Maestro E2E flows, read from the launch intent extras
 * (`launchApp: arguments:`). Everything is off unless a flow explicitly passes it,
 * so regular launches are unaffected.
 */
@Singleton
class E2eTestFlags @Inject constructor() {

    /**
     * When true, the backend is simulated as enforcing authentication: signed-out
     * `session/{room}` requests are answered with `401` without reaching the network.
     */
    @Volatile
    var forceAuthRequired: Boolean = false
        private set

    fun updateFrom(intent: Intent?) {
        forceAuthRequired = intent.readFlag(FORCE_AUTH_REQUIRED_EXTRA)
    }

    private fun Intent?.readFlag(key: String): Boolean {
        val extras = this?.extras ?: return false
        // Maestro passes YAML booleans as Boolean extras and quoted values as String extras.
        return extras.getBoolean(key, false) || extras.getString(key) == "true"
    }

    companion object {
        const val FORCE_AUTH_REQUIRED_EXTRA = "e2eForceAuthRequired"
    }
}
