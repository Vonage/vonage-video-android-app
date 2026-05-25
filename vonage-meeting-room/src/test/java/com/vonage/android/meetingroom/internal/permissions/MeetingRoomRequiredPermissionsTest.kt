package com.vonage.android.meetingroom.internal.permissions

import android.Manifest
import android.os.Build
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MeetingRoomRequiredPermissionsTest {

    @Test
    fun `given api 32 THEN returns only camera and audio`() {
        val permissions = computeRequiredPermissions(sdkInt = Build.VERSION_CODES.S_V2)

        assertEquals(listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), permissions)
    }

    @Test
    fun `given api 33 THEN returns camera audio notifications and bluetooth`() {
        val permissions = computeRequiredPermissions(sdkInt = Build.VERSION_CODES.TIRAMISU)

        assertEquals(
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.BLUETOOTH_CONNECT,
            ),
            permissions,
        )
    }

    @Test
    fun `given api 34 THEN returns camera audio notifications and bluetooth`() {
        val permissions = computeRequiredPermissions(sdkInt = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)

        assertEquals(
            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.BLUETOOTH_CONNECT,
            ),
            permissions,
        )
    }

    @Test
    fun `given any api level THEN camera is always first`() {
        listOf(24, 28, 31, 33, 35).forEach { sdkInt ->
            val permissions = computeRequiredPermissions(sdkInt = sdkInt)
            assertEquals(Manifest.permission.CAMERA, permissions.first())
        }
    }

    @Test
    fun `given api below 33 THEN no post notifications permission`() {
        val permissions = computeRequiredPermissions(sdkInt = 32)

        assertFalse(permissions.contains(Manifest.permission.POST_NOTIFICATIONS))
    }

    @Test
    fun `given api below 33 THEN no bluetooth connect permission`() {
        val permissions = computeRequiredPermissions(sdkInt = 32)

        assertFalse(permissions.contains(Manifest.permission.BLUETOOTH_CONNECT))
    }

    @Test
    fun `given api 33 THEN contains post notifications`() {
        val permissions = computeRequiredPermissions(sdkInt = 33)

        assertTrue(permissions.contains(Manifest.permission.POST_NOTIFICATIONS))
    }

    @Test
    fun `given api 33 THEN contains bluetooth connect`() {
        val permissions = computeRequiredPermissions(sdkInt = 33)

        assertTrue(permissions.contains(Manifest.permission.BLUETOOTH_CONNECT))
    }
}
