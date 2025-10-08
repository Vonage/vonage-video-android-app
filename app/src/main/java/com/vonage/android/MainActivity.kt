package com.vonage.android

import android.bluetooth.BluetoothHealthAppConfiguration
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.config.AppConfig
import com.vonage.android.navigation.AppNavHost
import com.vonage.android.notifications.VeraNotificationChannelRegistry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notificationChannelRegistry: VeraNotificationChannelRegistry

    private val flow = MutableSharedFlow<Intent>(extraBufferCapacity = 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        notificationChannelRegistry.createNotificationChannels()
        enableEdgeToEdge()
        Log.i("CONFIG FILE", "--> Video calling: ${AppConfig.VideoSettings.DEFAULT_RESOLUTION}")
        setContent {
            VonageVideoTheme {
                InterceptorAppNavHost(flow)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        flow.tryEmit(intent)
    }
}

@Composable
fun InterceptorAppNavHost(intentFlow: Flow<Intent>) {
    val navController = rememberNavController()

    LaunchedEffect(intentFlow) {
        intentFlow.collectLatest {
            it.data?.let { uri ->
                val request = NavDeepLinkRequest.Builder
                    .fromUri(uri)
                    .build()

                navController.navigate(
                    request,
                    navOptions = NavOptions.Builder().setLaunchSingleTop(true).build()
                )
            }
        }
    }

    AppNavHost(navController = navController)
}
