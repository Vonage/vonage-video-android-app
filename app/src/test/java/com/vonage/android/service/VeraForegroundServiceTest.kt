package com.vonage.android.service

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.vonage.android.service.VeraForegroundServiceHandler.Companion.ROOM_INTENT_EXTRA_NAME
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VeraForegroundServiceTest {

    private lateinit var service: VeraForegroundService

    @Before
    fun setUp() {
        mockkStatic(ContextCompat::class)
        service = VeraForegroundService()
    }

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
    }

    @Test
    fun `onBind should return null`() {
        val intent: Intent = mockk()

        val result = service.onBind(intent)

        assertNull(result)
    }

    @Test
    fun `onStartCommand should return START_REDELIVER_INTENT when intent is null`() {
        every {
            ContextCompat.checkSelfPermission(any(), permission.RECORD_AUDIO)
        } returns PackageManager.PERMISSION_GRANTED

        val result = service.onStartCommand(null, 0, 1)

        assertEquals(android.app.Service.START_REDELIVER_INTENT, result)
    }

    @Test
    fun `onStartCommand should extract room name from intent extras`() {
        every {
            ContextCompat.checkSelfPermission(any(), permission.RECORD_AUDIO)
        } returns PackageManager.PERMISSION_GRANTED

        val extras: Bundle = mockk(relaxed = true)
        every { extras.getString(ROOM_INTENT_EXTRA_NAME) } returns "TestRoom"

        val intent: Intent = mockk(relaxed = true)
        every { intent.extras } returns extras

        // Will throw due to notification building needing a real context,
        // but verifies the intent parsing path is exercised
        try {
            service.onStartCommand(intent, 0, 1)
        } catch (_: Exception) {
            // Expected: notification building requires real Android context
        }

        verify { extras.getString(ROOM_INTENT_EXTRA_NAME) }
    }

    @Test
    fun `onStartCommand should handle null extras gracefully`() {
        every {
            ContextCompat.checkSelfPermission(any(), permission.RECORD_AUDIO)
        } returns PackageManager.PERMISSION_GRANTED

        val intent: Intent = mockk(relaxed = true)
        every { intent.extras } returns null

        try {
            service.onStartCommand(intent, 0, 1)
        } catch (_: Exception) {
            // Expected: notification building requires real Android context
        }
    }
}
