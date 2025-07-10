package com.vonage.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vonage.android.screen.WaitingRoomScreen
import com.vonage.android.screen.join.JoinMeetingRoomRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavigationItem.JoinRoom.route,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.JoinRoom.route) {
            JoinMeetingRoomRoute(
                navigateToRoom = { params ->
                    navController.navigate(NavigationItem.WaitingRoom.route)
                },
            )
        }
        composable(NavigationItem.WaitingRoom.route) {
            WaitingRoomScreen()
        }
    }
}
