package com.vonage.android

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.navigation.AppNavHost
import com.vonage.android.screensharing.ScreenSharingManager
import com.vonage.android.screensharing.ScreenSharingService
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var screenSharingManager: ScreenSharingManager? = null
    var mediaProjectionManager: MediaProjectionManager? = null
    var mediaProjection: MediaProjection? = null

    val startMediaProjection = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            screenSharingManager = ScreenSharingManager(this.applicationContext)
            val serviceIntent = Intent(this, ScreenSharingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            }

            bindService(serviceIntent, object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    mediaProjection = mediaProjectionManager?.getMediaProjection(result.resultCode, result.data!!)
                    //val screenSharingCapturer = ScreenSharingCapturer(this@MainActivity, mediaProjection)
                }

                override fun onServiceDisconnected(name: ComponentName?) {

                }

            }, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        setContent {
            VonageVideoTheme {
                AppNavHost(navController = rememberNavController())
            }
        }

//        mediaProjectionManager = getSystemService(MediaProjectionManager::class.java)
//        mediaProjectionManager?.let {
//            startMediaProjection.launch(it.createScreenCaptureIntent())
//        }
    }
}
