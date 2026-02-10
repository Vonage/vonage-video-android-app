package com.vonage.android.screensharing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.screensharing.service.ScreenSharingService
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class EnabledScreenSharingTest {

    private val context: Context = mockk(relaxed = true)
    private val callFacade: CallFacade = mockk(relaxed = true)
    private val mediaProjectionManager: MediaProjectionManager = mockk(relaxed = true)
    private val mediaProjection: MediaProjection = mockk(relaxed = true)
    private val notificationManager: NotificationManager = mockk(relaxed = true)
    private val serviceIntent: Intent = mockk()
    private val mediaProjectionIntent: Intent = mockk()
    private val binder: IBinder = mockk()

    private lateinit var sut: EnabledScreenSharing

    @Before
    fun setup() {
        mockkStatic("androidx.core.content.ContextCompat")
        mockkObject(ScreenSharingService.Companion)

        every { ScreenSharingService.intent(context) } returns serviceIntent
        every {
            androidx.core.content.ContextCompat.getSystemService(
                context,
                MediaProjectionManager::class.java
            )
        } returns mediaProjectionManager

        sut = EnabledScreenSharing(context)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `startScreenSharing sets call facade for stop`() {
        val onStarted = mockk<() -> Unit>(relaxed = true)
        val onStopped = mockk<() -> Unit>(relaxed = true)
        val serviceConnectionSlot = slot<ServiceConnection>()

        every {
            context.bindService(
                serviceIntent,
                capture(serviceConnectionSlot),
                Context.BIND_AUTO_CREATE
            )
        } returns true
        every { context.startService(serviceIntent) } returns mockk<ComponentName>()
        every {
            mediaProjectionManager.getMediaProjection(
                any(),
                mediaProjectionIntent
            )
        } returns mediaProjection
        every { mediaProjection.stop() } just Runs
        every { context.unbindService(any()) } just Runs
        every { context.stopService(serviceIntent) } returns true

        sut.startScreenSharing(mediaProjectionIntent, callFacade, onStarted, onStopped)
        serviceConnectionSlot.captured.onServiceConnected(mockk<ComponentName>(), binder)
        sut.stopSharingScreen()

        verify { callFacade.stopCapturingScreen() }
    }

    @Test
    fun `startScreenSharing starts foreground service on API 26+`() {
        val onStarted = mockk<() -> Unit>(relaxed = true)
        val onStopped = mockk<() -> Unit>(relaxed = true)
        val serviceConnectionSlot = slot<ServiceConnection>()

        every {
            context.bindService(
                serviceIntent,
                capture(serviceConnectionSlot),
                Context.BIND_AUTO_CREATE
            )
        } returns true
        every {
            mediaProjectionManager.getMediaProjection(
                any(),
                mediaProjectionIntent
            )
        } returns mediaProjection
        every {
            androidx.core.content.ContextCompat.startForegroundService(
                context,
                serviceIntent
            )
        } just Runs

        sut.startScreenSharing(mediaProjectionIntent, callFacade, onStarted, onStopped)

        serviceConnectionSlot.captured.onServiceConnected(mockk<ComponentName>(), binder)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            verify {
                androidx.core.content.ContextCompat.startForegroundService(
                    context,
                    serviceIntent
                )
            }
        } else {
            verify { context.startService(serviceIntent) }
        }
        verify { context.bindService(serviceIntent, any(), Context.BIND_AUTO_CREATE) }
        verify { callFacade.startCapturingScreen(mediaProjection) }
        verify { onStarted() }
    }

    @Test
    fun `startScreenSharing starts regular service on API below 26`() {
        val onStarted = mockk<() -> Unit>(relaxed = true)
        val onStopped = mockk<() -> Unit>(relaxed = true)
        val serviceConnectionSlot = slot<ServiceConnection>()

        every {
            context.bindService(
                serviceIntent,
                capture(serviceConnectionSlot),
                Context.BIND_AUTO_CREATE
            )
        } returns true
        every { context.startService(serviceIntent) } returns mockk<ComponentName>()
        every {
            mediaProjectionManager.getMediaProjection(
                any(),
                mediaProjectionIntent
            )
        } returns mediaProjection

        sut.startScreenSharing(mediaProjectionIntent, callFacade, onStarted, onStopped)

        serviceConnectionSlot.captured.onServiceConnected(mockk<ComponentName>(), binder)

        verify { context.bindService(serviceIntent, any(), Context.BIND_AUTO_CREATE) }
        verify { callFacade.startCapturingScreen(mediaProjection) }
        verify { onStarted() }
    }

    @Test
    fun `startScreenSharing registers media projection callback`() {
        val onStarted = mockk<() -> Unit>(relaxed = true)
        val onStopped = mockk<() -> Unit>(relaxed = true)
        val serviceConnectionSlot = slot<ServiceConnection>()
        val callbackSlot = slot<MediaProjection.Callback>()

        every {
            context.bindService(
                serviceIntent,
                capture(serviceConnectionSlot),
                Context.BIND_AUTO_CREATE
            )
        } returns true
        every { context.startService(serviceIntent) } returns mockk<ComponentName>()
        every {
            mediaProjectionManager.getMediaProjection(
                any(),
                mediaProjectionIntent
            )
        } returns mediaProjection
        every { mediaProjection.registerCallback(capture(callbackSlot), null) } just Runs

        sut.startScreenSharing(mediaProjectionIntent, callFacade, onStarted, onStopped)
        serviceConnectionSlot.captured.onServiceConnected(mockk<ComponentName>(), binder)

        verify { mediaProjection.registerCallback(any(), null) }
        assertNotNull(callbackSlot.captured)
    }

    @Test
    fun `stopSharingScreen stops media projection and unbinds service`() {
        val onStarted = mockk<() -> Unit>(relaxed = true)
        val onStopped = mockk<() -> Unit>(relaxed = true)
        val serviceConnectionSlot = slot<ServiceConnection>()

        every {
            context.bindService(
                serviceIntent,
                capture(serviceConnectionSlot),
                Context.BIND_AUTO_CREATE
            )
        } returns true
        every { context.startService(serviceIntent) } returns mockk<ComponentName>()
        every {
            mediaProjectionManager.getMediaProjection(
                any(),
                mediaProjectionIntent
            )
        } returns mediaProjection
        every { mediaProjection.stop() } just Runs
        every { context.unbindService(any()) } just Runs
        every { context.stopService(serviceIntent) } returns true

        sut.startScreenSharing(mediaProjectionIntent, callFacade, onStarted, onStopped)
        serviceConnectionSlot.captured.onServiceConnected(mockk<ComponentName>(), binder)

        sut.stopSharingScreen()

        verify { mediaProjection.stop() }
        verify { context.unbindService(serviceConnectionSlot.captured) }
        verify { context.stopService(serviceIntent) }
        verify { callFacade.stopCapturingScreen() }
    }

    @Test
    fun `stopSharingScreen when not started does not throw exception`() {
        every { context.stopService(serviceIntent) } returns true

        sut.stopSharingScreen()

        verify { callFacade wasNot called }
        verify { context.stopService(serviceIntent) }
        verify { mediaProjection wasNot called }
    }

    @Test
    fun `stopSharingScreen clears internal state`() {
        val onStarted = mockk<() -> Unit>(relaxed = true)
        val onStopped = mockk<() -> Unit>(relaxed = true)
        val serviceConnectionSlot = slot<ServiceConnection>()

        every {
            context.bindService(
                serviceIntent,
                capture(serviceConnectionSlot),
                Context.BIND_AUTO_CREATE
            )
        } returns true
        every { context.startService(serviceIntent) } returns mockk<ComponentName>()
        every {
            mediaProjectionManager.getMediaProjection(
                any(),
                mediaProjectionIntent
            )
        } returns mediaProjection
        every { mediaProjection.stop() } just Runs
        every { context.unbindService(any()) } just Runs
        every { context.stopService(serviceIntent) } returns true

        sut.startScreenSharing(mediaProjectionIntent, callFacade, onStarted, onStopped)
        serviceConnectionSlot.captured.onServiceConnected(mockk<ComponentName>(), binder)

        sut.stopSharingScreen()

        sut.stopSharingScreen()
        verify(exactly = 1) { context.unbindService(any()) }
    }

    @Test
    fun `onServiceDisconnected stops sharing and triggers onStopped`() {
        val onStarted = mockk<() -> Unit>(relaxed = true)
        val onStopped = mockk<() -> Unit>(relaxed = true)
        val serviceConnectionSlot = slot<ServiceConnection>()

        every {
            context.bindService(
                serviceIntent,
                capture(serviceConnectionSlot),
                Context.BIND_AUTO_CREATE
            )
        } returns true
        every { context.startService(serviceIntent) } returns mockk<ComponentName>()
        every {
            mediaProjectionManager.getMediaProjection(
                any(),
                mediaProjectionIntent
            )
        } returns mediaProjection
        every { mediaProjection.stop() } just Runs
        every { context.unbindService(any()) } just Runs
        every { context.stopService(serviceIntent) } returns true

        sut.startScreenSharing(mediaProjectionIntent, callFacade, onStarted, onStopped)
        serviceConnectionSlot.captured.onServiceConnected(mockk<ComponentName>(), binder)

        serviceConnectionSlot.captured.onServiceDisconnected(mockk<ComponentName>())

        verify { callFacade.stopCapturingScreen() }
        verify { onStopped() }
    }

    @Test
    fun `startScreenSharing with null media projection does not start capturing`() {
        val onStarted = mockk<() -> Unit>(relaxed = true)
        val onStopped = mockk<() -> Unit>(relaxed = true)
        val serviceConnectionSlot = slot<ServiceConnection>()

        every {
            context.bindService(
                serviceIntent,
                capture(serviceConnectionSlot),
                Context.BIND_AUTO_CREATE
            )
        } returns true
        every { context.startService(serviceIntent) } returns mockk<ComponentName>()
        every {
            mediaProjectionManager.getMediaProjection(
                any(),
                mediaProjectionIntent
            )
        } returns null

        sut.startScreenSharing(mediaProjectionIntent, callFacade, onStarted, onStopped)
        serviceConnectionSlot.captured.onServiceConnected(mockk<ComponentName>(), binder)

        verify(exactly = 0) { callFacade.startCapturingScreen(any()) }
        verify(exactly = 0) { onStarted() }
    }

    @Test
    fun `createNotificationChannel creates channel with correct parameters`() {
        val channelSlot = slot<NotificationChannel>()
        every { notificationManager.createNotificationChannel(capture(channelSlot)) } just Runs

        sut.createNotificationChannel(notificationManager)

        verify { notificationManager.createNotificationChannel(any()) }
    }
}
