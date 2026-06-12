package com.vonage.android.meetingroom.internal.permissions

import android.Manifest
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.meetingroom.R
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultPermissionContentTest {

    @get:Rule(order = 0)
    val compose = createComposeRule()

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun getString(id: Int): String = compose.activity.getString(id)

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    fun given_emptyPermissions_THEN_onGranted_called_immediately() {
        var granted = false

        compose.setContent {
            VonageVideoTheme {
                DefaultPermissionContent(
                    permissions = emptyList(),
                    onGrant = { granted = true },
                )
            }
        }

        compose.waitForIdle()
        assertTrue("onGranted should have been called for empty permissions list", granted)
    }

    @Test
    fun given_allPermissionsGranted_THEN_onGranted_called_without_dialog() {
        var granted = false
        val grantRule = GrantPermissionRule.grant(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
        )
        grantRule.apply(
            org.junit.runners.model.Statement { },
            org.junit.runner.Description.EMPTY,
        ).evaluate()

        compose.setContent {
            VonageVideoTheme {
                DefaultPermissionContent(
                    permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    onGrant = { granted = true },
                )
            }
        }

        compose.waitForIdle()
        // Dialog title should not be visible when permissions are already granted
        compose.onNodeWithText(getString(R.string.meeting_room_permission_required_title))
            .assertDoesNotExist()
        assertTrue("onGranted should have been called when permissions are pre-granted", granted)
    }

    @Test
    fun given_permissionsDenied_THEN_rationale_or_settings_dialog_shown() {
        // Without GrantPermissionRule, CAMERA and RECORD_AUDIO are not pre-granted.
        // After the system dialog closes (auto-denied in test environment), the SDK dialog appears.
        compose.setContent {
            VonageVideoTheme {
                DefaultPermissionContent(
                    permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    onGrant = { },
                )
            }
        }

        // The permission dialog launches automatically. In a test environment the system dialog
        // is denied (permissions not pre-granted). The SDK dialog should then appear.
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodes(
                androidx.compose.ui.test.hasText(getString(R.string.meeting_room_permission_required_title)),
            ).fetchSemanticsNodes().isNotEmpty()
        }

        compose.onNodeWithText(getString(R.string.meeting_room_permission_required_title))
            .assertIsDisplayed()
    }
}
