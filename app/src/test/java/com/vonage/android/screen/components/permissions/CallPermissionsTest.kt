package com.vonage.android.screen.components.permissions

import android.Manifest
import android.os.Build
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CallPermissionsTest {

    @ParameterizedTest
    @MethodSource("permissionsByApiLevel")
    fun `given api level THEN returns the runtime permissions it needs`(sdkInt: Int, expected: List<String>) {
        assertEquals(expected, callPermissions(sdkInt))
    }

    companion object {
        private val base = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        private val withBluetooth = base + Manifest.permission.BLUETOOTH_CONNECT
        private val withNotifications = withBluetooth + Manifest.permission.POST_NOTIFICATIONS

        @JvmStatic
        fun permissionsByApiLevel() = listOf(
            Arguments.of(Build.VERSION_CODES.N, base),
            Arguments.of(Build.VERSION_CODES.R, base),
            Arguments.of(Build.VERSION_CODES.S, withBluetooth),
            Arguments.of(Build.VERSION_CODES.S_V2, withBluetooth),
            Arguments.of(Build.VERSION_CODES.TIRAMISU, withNotifications),
            Arguments.of(Build.VERSION_CODES.UPSIDE_DOWN_CAKE, withNotifications),
        )
    }
}
