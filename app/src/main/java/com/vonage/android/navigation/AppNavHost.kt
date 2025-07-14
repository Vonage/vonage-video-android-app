package com.vonage.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vonage.android.screen.RoomScreen
import com.vonage.android.screen.join.JoinMeetingRoomRoute
import com.vonage.android.screen.waiting.WaitingRoomRoute
import com.vonage.android.util.navigateToSystemPermissions

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavigationItem.JoinRoom.route,
) {
    val context = LocalContext.current
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationItem.JoinRoom.route) {
            JoinMeetingRoomRoute(
                navigateToRoom = { params ->
                    navController.navigate(
                        NavigationItem.WaitingRoom.createRoute(
                            roomName = params.roomName,
                            apiKey = params.apiKey,
                            sessionId = params.sessionId,
                            token = params.token,
                        )
                    )
                },
            )
        }
        composable(
            route = NavigationItem.WaitingRoom.route,
            arguments = listOf(
                navArgument("roomName") { type = NavType.StringType },
                navArgument("apiKey") { type = NavType.StringType },
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val roomName = backStackEntry.arguments?.getString("roomName")
            WaitingRoomRoute(
                roomName = roomName.toString(),
                navigateToRoom = { roomName ->
                    navController.navigate(
                        NavigationItem.Room.createRoute(
                            roomName = roomName,
                        )
                    )
                },
                navigateToPermissions = {
                    navigateToSystemPermissions(context)
                },
            )
        }
        composable(
            route = NavigationItem.Room.route,
            arguments = listOf(
                navArgument("roomName") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val roomName = backStackEntry.arguments?.getString("roomName")
            RoomScreen(
                roomName = roomName.toString(),
            )
        }
    }
}
