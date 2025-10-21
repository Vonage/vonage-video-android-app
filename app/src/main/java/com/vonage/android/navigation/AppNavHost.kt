package com.vonage.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.telefonica.tweaks.addTweakGraph
import com.vonage.android.BuildConfig
import com.vonage.android.navigation.AppRoute.Goodbye
import com.vonage.android.navigation.AppRoute.Landing
import com.vonage.android.navigation.AppRoute.Meeting
import com.vonage.android.navigation.AppRoute.Waiting
import com.vonage.android.screen.goodbye.GoodbyeScreenRoute
import com.vonage.android.screen.join.JoinMeetingRoomRoute
import com.vonage.android.screen.room.MeetingRoomScreenRoute
import com.vonage.android.screen.waiting.WaitingRoomRoute
import com.vonage.android.util.navigateToShare
import com.vonage.android.util.navigateToSystemPermissions

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Landing,
    ) {
        composable<Landing> {
            JoinMeetingRoomRoute(
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
                navigateToRoom = { roomName ->
                    navController.navigate(
                        route = Meeting(roomName),
                        navOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
                    )
                },
                navigateToPermissions = { context.navigateToSystemPermissions() },
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
                navigateToGoodBye = { navController.navigate(Goodbye(roomName = roomName)) },
                navigateToShare = { roomName -> context.navigateToShare(roomName) },
                onBack = {
                    navController.navigate(Waiting(roomName = roomName)) {
                        popUpTo(Waiting(roomName = roomName)) { inclusive = true }
                    }
                }
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
        addTweakGraph(
            navController = navController,
        )
    }
}
