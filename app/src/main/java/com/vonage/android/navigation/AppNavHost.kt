package com.vonage.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vonage.android.screen.GoodbyeScreen
import com.vonage.android.screen.join.JoinMeetingRoomRoute
import com.vonage.android.screen.room.MeetingRoomScreenRoute
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
        composable(route = NavigationItem.JoinRoom.route) {
            JoinMeetingRoomRoute(
                navigateToRoom = { params ->
                    navController.navigate(
                        NavigationItem.WaitingRoom.createRoute(
                            roomName = params.roomName,
                        )
                    )
                },
            )
        }
        composable(
            route = NavigationItem.WaitingRoom.route,
            arguments = listOf(
                navArgument("roomName") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val roomName = backStackEntry.arguments?.getString("roomName")
            WaitingRoomRoute(
                roomName = roomName.toString(),
                navigateToRoom = { roomName ->
                    navController.navigate(
                        NavigationItem.MeetingRoom.createRoute(
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
            route = NavigationItem.MeetingRoom.route,
            arguments = listOf(
                navArgument("roomName") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val roomName = backStackEntry.arguments?.getString("roomName")
            MeetingRoomScreenRoute(
                roomName = roomName.toString(),
                navigateToGoodBye = {
                    navController.navigate(NavigationItem.GoodbyeRoom.route)
                }
            )
        }
        composable(
            route = NavigationItem.GoodbyeRoom.route,
        ) { backStackEntry ->
            GoodbyeScreen()
        }
    }
}
