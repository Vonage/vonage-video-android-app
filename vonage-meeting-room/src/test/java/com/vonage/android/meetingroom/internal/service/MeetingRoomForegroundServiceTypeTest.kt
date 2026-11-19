package com.vonage.android.meetingroom.internal.service

import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
import android.os.Build
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MeetingRoomForegroundServiceTypeTest {

    @Test
    fun `given camera permission THEN type is microphone and camera`() {
        assertEquals(
            FOREGROUND_SERVICE_TYPE_MICROPHONE or FOREGROUND_SERVICE_TYPE_CAMERA,
            foregroundServiceType(hasCameraPermission = true, sdkInt = Build.VERSION_CODES.UPSIDE_DOWN_CAKE),
        )
    }

    @Test
    fun `given no camera permission THEN type is microphone only`() {
        assertEquals(
            FOREGROUND_SERVICE_TYPE_MICROPHONE,
            foregroundServiceType(hasCameraPermission = false, sdkInt = Build.VERSION_CODES.UPSIDE_DOWN_CAKE),
        )
    }

    @Test
    fun `given api below 30 THEN type is none`() {
        assertEquals(0, foregroundServiceType(hasCameraPermission = true, sdkInt = Build.VERSION_CODES.Q))
    }
}
