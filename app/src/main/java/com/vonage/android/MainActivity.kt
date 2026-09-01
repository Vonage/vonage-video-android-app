package com.vonage.android

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Box
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.di.CallSettingsHolderEntryPoint
import com.vonage.android.di.VonageOktaAuthEntryPoint
import com.vonage.android.navigation.AppNavHost
import com.vonage.android.util.InAppUpdates
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val flow = MutableSharedFlow<Intent>(extraBufferCapacity = 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()

        // Register InApp updates
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                InAppUpdates(this@MainActivity).register()
            }
        }

        setContent {
            VonageVideoTheme {
                Box(
                    modifier = Modifier.semantics {
                        testTagsAsResourceId = true
                    }
                ) {
                    InterceptorAppNavHost(flow)
                }
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
    val context = LocalContext.current
    val navController = rememberNavController()
    val callSettingsHolder = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            CallSettingsHolderEntryPoint::class.java
        ).callSettingsHolder()
    }
    val oktaAuth = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            VonageOktaAuthEntryPoint::class.java
        ).vonageOktaAuth()
    }
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
    AppNavHost(navController = navController, callSettingsHolder = callSettingsHolder, oktaAuth = oktaAuth)
}
