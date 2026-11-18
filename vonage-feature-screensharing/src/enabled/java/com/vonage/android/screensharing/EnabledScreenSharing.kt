package com.vonage.android.screensharing

import android.app.Activity
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
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.screensharing.service.ScreenSharingOverlayService
import com.vonage.android.screensharing.service.ScreenSharingService

class EnabledScreenSharing(
    private val context: Context,
) : VonageScreenSharing {

    private val serviceIntent = ScreenSharingService.intent(context)
    private val overlayIntent = ScreenSharingOverlayService.intent(context)
    private val mediaProjectionManager: MediaProjectionManager =
        getSystemService(context, MediaProjectionManager::class.java) as MediaProjectionManager

    private var currentMediaProjection: MediaProjection? = null
    private var screenSharingServiceConnection: ServiceConnection? = null

    private var call: CallFacade? = null

    override fun startScreenSharing(
        intent: Intent,
        call: CallFacade,
        onStarted: () -> Unit,
        onStopped: () -> Unit
    ) {
        this.call = call
        val mediaProjectionCallback = object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                stopSharingScreen()
                onStopped()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(context, serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        screenSharingServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, intent)
                    ?.let { mediaProjection ->
                        currentMediaProjection = mediaProjection
                        currentMediaProjection?.registerCallback(mediaProjectionCallback, null)
                        call.startCapturingScreen(mediaProjection)
                        showOverlay()
                        onStarted()
                    }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                stopSharingScreen()
                onStopped()
            }
        }

        context.bindService(
            serviceIntent,
            screenSharingServiceConnection!!,
            Context.BIND_AUTO_CREATE
        )
    }

    @Synchronized
    override fun stopSharingScreen() {
        hideOverlay()
        currentMediaProjection?.stop()
        screenSharingServiceConnection?.let {
            context.unbindService(it)
            screenSharingServiceConnection = null
        }
        context.stopService(serviceIntent)
        call?.stopCapturingScreen()
        currentMediaProjection = null
        call = null
    }

    override fun canDrawOverlays(): Boolean =
        Settings.canDrawOverlays(context)

    override fun showOverlay() {
        if (canDrawOverlays()) {
            context.startService(overlayIntent)
        }
    }

    override fun hideOverlay() {
        context.stopService(overlayIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun createNotificationChannel(manager: NotificationManager) {
        val screenSharingChannel = NotificationChannel(
            ScreenSharingService.NOTIFICATION_CHANNEL_ID,
            ScreenSharingService.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        manager.createNotificationChannel(screenSharingChannel)
    }
}
