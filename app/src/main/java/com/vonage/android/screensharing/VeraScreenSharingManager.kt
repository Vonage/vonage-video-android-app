package com.vonage.android.screensharing

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import com.vonage.android.kotlin.model.CallFacade
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class VeraScreenSharingManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    var mediaProjectionManager: MediaProjectionManager =
        getSystemService(context, MediaProjectionManager::class.java) as MediaProjectionManager

    fun startScreenSharing(data: Intent, call: CallFacade?) {
        val serviceIntent = Intent(context, ScreenSharingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(context, serviceIntent)
        }

        context.bindService(serviceIntent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, data)
                    ?.let { call?.startCapturingScreen(it) }
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }

        }, Context.BIND_AUTO_CREATE)
    }
}
