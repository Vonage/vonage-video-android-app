package com.vonage.android.navigation

import androidx.compose.runtime.Composable
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
import com.vonage.android.kotlin.model.VideoEffect
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
import com.vonage.android.util.navigateToShare
import com.vonage.android.util.navigateToSystemPermissions

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var pendingVideoEffect by remember { mutableStateOf<VideoEffect>(VideoEffect.None) }
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Landing,
    ) {
        composable<Landing> {
            LandingScreenRoute(
                navigateToRoom = { params -> navController.navigate(Waiting(params.roomName)) },
            )
        }
        composable<Waiting>(
            deepLinks = listOf(
                navDeepLink<Waiting>("${BuildConfig.BASE_API_URL}/waiting-room"),
            )
        ) { backStackEntry ->
            val roomName = backStackEntry.toRoute<Waiting>().roomName
            WaitingRoomRoute(
                roomName = roomName,
                navigateToRoom = { roomName, videoEffect ->
                    pendingVideoEffect = videoEffect
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
                navDeepLink<Meeting>("${BuildConfig.BASE_API_URL}/room"),
            )
        ) { backStackEntry ->
            val roomName = backStackEntry.toRoute<Meeting>().roomName
            MeetingRoomScreenRoute(
                roomName = roomName,
                initialVideoEffect = pendingVideoEffect,
                navigateToGoodBye = { navController.navigate(Goodbye(roomName = roomName)) },
                navigateToShare = { roomName -> context.navigateToShare(roomName) },
                navigateToSettings = { navController.navigate(Settings) },
            )
        }
        composable<Goodbye> { backStackEntry ->
            val roomName = backStackEntry.toRoute<Goodbye>().roomName
            GoodbyeScreenRoute(
                roomName = roomName,
                navigateToMeeting = { roomName -> navController.navigate(Meeting(roomName = roomName)) },
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
        ){
            SettingsScreenRoute(
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}
