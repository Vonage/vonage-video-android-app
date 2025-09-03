package com.vonage.android.screensharing

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class VeraScreenSharingManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    private val serviceIntent = ScreenSharingService.intent(context)
    private val mediaProjectionManager: MediaProjectionManager =
        getSystemService(context, MediaProjectionManager::class.java) as MediaProjectionManager

    private var currentMediaProjection: MediaProjection? = null
    private var screenSharingServiceConnection: ServiceConnection? = null

    fun startScreenSharing(data: Intent, listener: ScreenSharingServiceListener) {
        val mediaProjectionCallback = object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                stopSharingScreen()
                listener.onStopped()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(context, serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        screenSharingServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, data)
                    ?.let { mediaProjection ->
                        currentMediaProjection = mediaProjection
                        currentMediaProjection?.registerCallback(mediaProjectionCallback, null)
                        listener.onStarted(mediaProjection)
                    }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                stopSharingScreen()
                listener.onStopped()
            }
        }

        context.bindService(serviceIntent, screenSharingServiceConnection!!, Context.BIND_AUTO_CREATE)
    }

    @Synchronized
    fun stopSharingScreen() {
        currentMediaProjection?.stop()
        screenSharingServiceConnection?.let {
            context.unbindService(it)
            screenSharingServiceConnection = null
        }
        context.stopService(serviceIntent)
        currentMediaProjection = null
    }
}

interface ScreenSharingServiceListener {
    fun onStarted(mediaProjection: MediaProjection)
    fun onStopped()
}
