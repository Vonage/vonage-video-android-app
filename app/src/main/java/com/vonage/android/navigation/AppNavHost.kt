package com.vonage.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
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
        composable<Waiting> { backStackEntry ->
            val params: Waiting = backStackEntry.toRoute()
            WaitingRoomRoute(
                roomName = params.roomName,
                navigateToRoom = { roomName -> navController.navigate(Meeting(roomName)) },
                navigateToPermissions = { context.navigateToSystemPermissions() },
                onBack = {
                    navController.navigate(Landing) {
                        popUpTo(Landing) { inclusive = true }
                    }
                },
            )
        }
        composable<Meeting> { backStackEntry ->
            val params: Meeting = backStackEntry.toRoute()
            MeetingRoomScreenRoute(
                roomName = params.roomName,
                navigateToGoodBye = { navController.navigate(Goodbye(roomName = params.roomName)) },
                navigateToShare = { roomName -> context.navigateToShare(roomName) },
                onBack = {
                    navController.navigate(Waiting(roomName = params.roomName)) {
                        popUpTo(Waiting(roomName = params.roomName)) { inclusive = true }
                    }
                }
            )
        }
        composable<Goodbye> { backStackEntry ->
            val params: Goodbye = backStackEntry.toRoute()
            GoodbyeScreenRoute(
                roomName = params.roomName,
                navigateToMeeting = { roomName -> navController.navigate(Meeting(roomName = params.roomName)) },
                navigateToLanding = {
                    navController.navigate(Landing) {
                        popUpTo(Landing) { inclusive = true }
                    }
                },
            )
        }
    }
}
