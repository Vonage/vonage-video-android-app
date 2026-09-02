package com.vonage.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.vonage.android.BuildConfig
import com.vonage.android.meetingroom.api.MeetingRoomAuthTokenProvider
import com.vonage.android.meetingroom.api.PublisherSettings
import com.vonage.android.okta.VonageOktaAuth
import com.vonage.android.navigation.AppRoute.Goodbye
import com.vonage.android.navigation.AppRoute.Landing
import com.vonage.android.navigation.AppRoute.Meeting
import com.vonage.android.navigation.AppRoute.Settings
import com.vonage.android.navigation.AppRoute.Waiting
import com.vonage.android.screen.goodbye.GoodbyeScreenRoute
import com.vonage.android.screen.landing.LandingScreenRoute
import com.vonage.android.screen.room.MeetingRoomScreenRoute
import com.vonage.android.screen.settings.SettingsScreenRoute
import com.vonage.android.screen.waiting.WaitingRoomRoute
import com.vonage.android.settings.CallSettingsHolder
import com.vonage.android.util.navigateToShare
import com.vonage.android.util.navigateToSystemPermissions
import kotlinx.coroutines.runBlocking

@Composable
fun AppNavHost(
    navController: NavHostController,
    callSettingsHolder: CallSettingsHolder,
    oktaAuth: VonageOktaAuth,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var pendingPublisherSettings by remember { mutableStateOf<PublisherSettings?>(null) }
    val authTokenProvider = remember(oktaAuth) {
        // Called by OkHttp on a background thread, so blocking is safe here.
        MeetingRoomAuthTokenProvider { runBlocking { oktaAuth.currentToken() } }
    }
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Landing,
    ) {
        composable<Landing> {
            LandingScreenRoute(
                oktaAuth = oktaAuth,
                navigateToRoom = { params -> navController.navigate(Waiting(params.roomName)) },
            )
        }
        composable<Waiting>(
            deepLinks = listOf(
                navDeepLink<Waiting>("$DEEP_LINK_BASE_URL/waiting-room"),
            )
        ) { backStackEntry ->
            val roomName = backStackEntry.toRoute<Waiting>().roomName
            WaitingRoomRoute(
                roomName = roomName,
                navigateToRoom = { roomName, settings ->
                    pendingPublisherSettings = settings
                    navController.navigate(
                        route = Meeting(roomName),
                        navOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
                    )
                },
                navigateToPermissions = { context.navigateToSystemPermissions() },
                navigateToSettings = { navController.navigate(Settings) },
                onBack = {
                    navController.navigate(Landing) {
                        popUpTo(Landing) { inclusive = true }
                    }
                },
            )
        }
        composable<Meeting>(
            deepLinks = listOf(
                navDeepLink<Meeting>("$DEEP_LINK_BASE_URL/room"),
            )
        ) { backStackEntry ->
            val roomName = backStackEntry.toRoute<Meeting>().roomName
            // Capture once per navigation event; re-entry that bypasses the waiting room
            // (e.g. Goodbye → Re-enter) will find null here and receive clean defaults.
            val settings = remember(roomName) { pendingPublisherSettings ?: PublisherSettings() }
            // Reset so any future arrival at Meeting without a waiting-room join gets defaults.
            SideEffect { pendingPublisherSettings = null }
            MeetingRoomScreenRoute(
                roomName = roomName,
                callSettingsHolder = callSettingsHolder,
                authTokenProvider = authTokenProvider,
                initialPublisherSettings = settings,
                navigateToGoodBye = { navController.navigate(Goodbye(roomName = roomName)) },
                navigateToShare = { roomName -> context.navigateToShare(roomName) },
                navigateToSettings = { navController.navigate(Settings) },
            )
        }
        composable<Goodbye> { backStackEntry ->
            val roomName = backStackEntry.toRoute<Goodbye>().roomName
            GoodbyeScreenRoute(
                roomName = roomName,
                navigateToWaiting = { roomName -> navController.navigate(Waiting(roomName = roomName)) },
                navigateToLanding = {
                    navController.navigate(Landing) {
                        popUpTo(Landing) { inclusive = true }
                    }
                },
            )
        }
        dialog<Settings>(
            dialogProperties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false,
            )
        ) {
            SettingsScreenRoute(
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}

/** BASE_API_URL may carry a trailing slash; deep-link patterns must not double it. */
private val DEEP_LINK_BASE_URL = BuildConfig.BASE_API_URL.trimEnd('/')
